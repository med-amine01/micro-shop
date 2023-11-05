package de.tekup.controller;

import de.tekup.dto.APIResponse;
import de.tekup.dto.InventoryRequestDTO;
import de.tekup.dto.InventoryResponseDTO;
import de.tekup.service.InventoryService;
import de.tekup.util.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {
    public static final String SUCCESS = "SUCCESS";
    private final InventoryService inventoryService;
    
    @GetMapping
    public ResponseEntity<APIResponse<List<InventoryResponseDTO>>> getAllInventories() {
        List<InventoryResponseDTO> inventories = inventoryService.getInventories();
        
        // Builder Design pattern (to avoid complex object creation)
        APIResponse<List<InventoryResponseDTO>> responseDTO = APIResponse
                .<List<InventoryResponseDTO>>builder()
                .status(SUCCESS)
                .results(inventories)
                .build();
        
        log.info("InventoryController::get inventories response {}", Mapper.jsonToString(responseDTO));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    
    @GetMapping("/product/init/{skuCode}")
    public ResponseEntity<APIResponse<InventoryResponseDTO>> initializeProductQuantity
            (
                    @PathVariable("skuCode") @NotBlank @Size(min = 2) String skuCode
            ) throws Exception {
        
        InventoryResponseDTO prodResponseDto = inventoryService.initQuantityFromQueue(skuCode);
        
        APIResponse<InventoryResponseDTO> responseDTO = APIResponse
                .<InventoryResponseDTO>builder()
                .status(SUCCESS)
                .results(prodResponseDto)
                .build();
        
        log.info("InventoryController::get inventories response {}", Mapper.jsonToString(responseDTO));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    
    
    // http://inventory-service/api/inventories/product/check?skuCode=iphone-13&skuCode=iphone13-red
    // http://inventory-service/api/v1/inventories/product/check?skuCode=iphone_13,iphone_13_red
    @GetMapping("/product/check")
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponseDTO> isInStock(@RequestParam List<String> skuCode) {
        return inventoryService.isInStock(skuCode);
    }
    
    @PutMapping("/product/quantity/{skuCode}")
    public ResponseEntity<APIResponse<InventoryResponseDTO>> updateInventoryQuantity
            (
                    @RequestBody @Valid InventoryRequestDTO requestDTO,
                    @PathVariable("skuCode") @NotBlank @Size(min = 2) String skuCode
            ) throws Exception {
        
        InventoryResponseDTO prodResponseDto = inventoryService.updateQuantity(requestDTO, skuCode);
        
        APIResponse<InventoryResponseDTO> responseDTO = APIResponse
                .<InventoryResponseDTO>builder()
                .status(SUCCESS)
                .results(prodResponseDto)
                .build();
        
        log.info("InventoryController::updateInventoryQuantity response {}", Mapper.jsonToString(responseDTO));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
