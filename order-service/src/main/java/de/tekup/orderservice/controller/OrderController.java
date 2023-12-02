package de.tekup.orderservice.controller;

import de.tekup.orderservice.dto.request.OrderRequest;
import de.tekup.orderservice.dto.request.OrderStatusRequest;
import de.tekup.orderservice.dto.response.ApiResponse;
import de.tekup.orderservice.dto.response.OrderResponse;
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
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByStatus
            (
                    @RequestParam(required = false) String status
            ) {
        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
        
        ApiResponse<List<OrderResponse>> responseDTO = ApiResponse
                .<List<OrderResponse>>builder()
                .status(SUCCESS)
                .results(orders)
                .build();
        
        log.info("OrderController::getOrdersByStatus response {}", Mapper.jsonToString(responseDTO));
        
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder
            (
                    @RequestBody @Valid OrderRequest orderRequest,
                    @RequestParam(required = false) String coupon,
                    @RequestHeader("Authorization") String authorization
            ) {
        ApiResponse<OrderResponse> responseDTO = ApiResponse
                .<OrderResponse>builder()
                .status(SUCCESS)
                .results(orderService.placeOrder(orderRequest, coupon, authorization))
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    
    @PatchMapping("/{uuid}")
    public ResponseEntity<ApiResponse<OrderResponse>> patchOrderStatus
            (
                    @RequestBody @Valid OrderStatusRequest requestStatus,
                    @PathVariable String uuid,
                    @RequestHeader("Authorization") String authorization
            ) {
        
        ApiResponse<OrderResponse> responseDTO = ApiResponse
                .<OrderResponse>builder()
                .status(SUCCESS)
                .results(orderService.updateOrderStatus(requestStatus, uuid, authorization))
                .build();
        
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }
}
