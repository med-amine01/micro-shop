package de.tekup.couponservice.service.serviceImpl;

import de.tekup.couponservice.exception.CouponServiceBusinessException;
import de.tekup.couponservice.service.CouponServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponSchedulerService {
    
    private Map<String, LocalDateTime> expCouponMap = new HashMap<>();
    private Map<String, ScheduledFuture<?>> scheduledTasks = new HashMap<>(); // Store ScheduledFuture objects
    
    private final TaskScheduler taskScheduler;
    private final CouponServiceInterface couponServiceInterface;
    
    public void prepare2ExecuteTasks() {
        Map<String, LocalDateTime> sortedExpCouponMap = sortMapByLocalDateTimeAsc(expCouponMap);
        
        for (Map.Entry<String, LocalDateTime> entry : sortedExpCouponMap.entrySet()) {
            ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(() -> {
                couponServiceInterface.disableCoupon(entry.getKey());
                log.info("Executing task for coupon code: " + entry.getKey());
            }, convertToInstant(entry.getValue()));
            
            // Store the ScheduledFuture in a map
            scheduledTasks.put(entry.getKey(), scheduledFuture);
            
            // Remove the code after schedule runs
            expCouponMap.remove(entry.getKey());
        }
    }
    
    public void cancelScheduledTask(String code) {
        ScheduledFuture<?> scheduledFuture = scheduledTasks.get(code);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            scheduledTasks.remove(code);
            expCouponMap.remove(code);
            log.info("Canceled task for coupon code: " + code);
        } else {
            log.warn("Task not found for coupon code: " + code);
        }
    }
    
    public void add2ExpirationDateMap(String code, String dateTime) {
        try {
            LocalDateTime expirationDateTime = LocalDateTime.parse(
                    dateTime.trim(),
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            );
            expCouponMap.put(code, expirationDateTime);
            log.info("Adding to MAP = {}", expCouponMap);
        } catch (Exception e) {
            throw new CouponServiceBusinessException(e.getMessage());
        }
    }
    
    private static Map<String, LocalDateTime> sortMapByLocalDateTimeAsc(Map<String, LocalDateTime> unsortedMap) {
        List<Map.Entry<String, LocalDateTime>> sortedEntries = unsortedMap
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .toList();
        
        Map<String, LocalDateTime> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, LocalDateTime> entry : sortedEntries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        
        return sortedMap;
    }
    
    private static Instant convertToInstant(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
}
