package de.tekup.couponservice.service.serviceImpl;

import de.tekup.couponservice.exception.CouponServiceBusinessException;
import de.tekup.couponservice.service.CouponServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponSchedulerService {
    
    private final TaskScheduler taskScheduler;
    private final CouponServiceInterface couponServiceInterface;
    private final RedisTemplate<String, String> redisTemplate;
    
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    
    public void prepare2ExecuteTasks() {
        redisTemplate.keys("*").forEach(code -> {
            String value = redisTemplate.opsForValue().get(code);
            LocalDateTime expirationDateTime = getExpirationDateTime(value);
            ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(() -> {
                log.info("Executing task for coupon code: <{} , {}>", code, value);
                
                couponServiceInterface.disableCoupon(code);
                scheduledTasks.remove(code);
                redisTemplate.delete(code);
                
                log.info("Task Executed");
            }, convertToInstant(expirationDateTime));
            
            // Store the ScheduledFuture in the map
            scheduledTasks.put(code, scheduledFuture);
            
            log.info("Task scheduled for coupon code: " + code);
        });
        
        log.info("Number of tasks scheduled: " + scheduledTasks.size());
    }

    
    public void cancelScheduledTask(String code) {
        ScheduledFuture<?> scheduledFuture = scheduledTasks.get(code);
        if (scheduledFuture != null) {
            // Cancel the scheduled task
            scheduledFuture.cancel(true);
            
            // Remove from the map and Redis cache
            scheduledTasks.remove(code);
            redisTemplate.delete(code);
            
            log.info("Canceled task for coupon code: " + code);
        } else {
            log.warn("Task not found for coupon code: " + code);
        }
    }
    
    public void saveTaskToRedis(String code, String dateTime) {
        try {
            // Store in Redis with key expiration
            redisTemplate.opsForValue().set(code, dateTime);

            log.info("Adding to Redis cache: code={}, expirationDateTime={}", code, dateTime);
        } catch (Exception e) {
            throw new CouponServiceBusinessException(e.getMessage());
        }
    }
    
    private static LocalDateTime getExpirationDateTime(String dateTime) {
        return LocalDateTime.parse(
                dateTime.trim(),
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        );
    }
    
    private static Instant convertToInstant(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
}
