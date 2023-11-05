package de.tekup.orderservice.controller;

import de.tekup.orderservice.dto.APIResponse;
import de.tekup.orderservice.dto.OrderRequest;
import de.tekup.orderservice.dto.OrderResponse;
import de.tekup.orderservice.dto.OrderStatusRequest;
import de.tekup.orderservice.service.OrderService;
import de.tekup.orderservice.util.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    
    public static final String SUCCESS = "SUCCESS";
    
    private final OrderService orderService;
    
    @GetMapping
    public ResponseEntity<APIResponse<List<OrderResponse>>> getOrdersByStatus
            (
                    @RequestParam(required = false) String status
            ) {
        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
        
        APIResponse<List<OrderResponse>> responseDTO = APIResponse
                .<List<OrderResponse>>builder()
                .status(SUCCESS)
                .results(orders)
                .build();
        
        log.info("OrderController::getOrdersByStatus response {}", Mapper.jsonToString(responseDTO));
        
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }
    
    @PostMapping
    public ResponseEntity<APIResponse<OrderResponse>> createProduct
            (
                    @RequestBody @Valid OrderRequest orderRequest,
                    @RequestParam(required = false) String coupon
            ) {
        APIResponse<OrderResponse> responseDTO = APIResponse
                .<OrderResponse>builder()
                .status(SUCCESS)
                .results(orderService.placeOrder(orderRequest, coupon))
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    
    @PatchMapping("/{uuid}")
    public ResponseEntity<APIResponse<OrderResponse>> patchOrderStatus
            (
                    @RequestBody @Valid OrderStatusRequest requestStatus,
                    @PathVariable String uuid
            ) {
        
        APIResponse<OrderResponse> responseDTO = APIResponse
                .<OrderResponse>builder()
                .status(SUCCESS)
                .results(orderService.updateOrderStatus(requestStatus, uuid))
                .build();
        
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }
}
