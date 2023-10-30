package de.tekup.orderservice.controller;

import de.tekup.orderservice.dto.APIResponse;
import de.tekup.orderservice.dto.OrderRequest;
import de.tekup.orderservice.dto.OrderResponse;
import de.tekup.orderservice.dto.OrderStatusRequest;
import de.tekup.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    
    public static final String SUCCESS = "SUCCESS";
    
    private final OrderService orderService;
    
    @PostMapping
    public ResponseEntity<APIResponse<OrderResponse>> createProduct
    (
        @RequestBody @Valid OrderRequest orderRequest,
        @RequestParam(required = false) String coupon
    )
    {
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
                .results(orderService.updateOrderStatus(requestStatus,uuid))
                .build();
        
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }
}
