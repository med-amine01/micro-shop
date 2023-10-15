package de.tekup.controller;

import de.tekup.dto.APIResponse;
import de.tekup.dto.InventoryRequestDTO;
import de.tekup.dto.InventoryResponseDTO;
import de.tekup.exception.InventoryNotFoundException;
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
                @PathVariable @NotBlank @Size(min = 2) String skuCode
            ) {

        InventoryResponseDTO prodResponseDto = inventoryService.initQuantityFromQueue(skuCode);
        
        APIResponse<InventoryResponseDTO> responseDTO = APIResponse
                .<InventoryResponseDTO>builder()
                .status(SUCCESS)
                .results(prodResponseDto)
                .build();
        
        log.info("InventoryController::get inventories response {}", Mapper.jsonToString(responseDTO));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    

    // http://localhost:8082/api/inventories/check?skuCode=iphone-13&skuCode=iphone13-red
    @GetMapping("/product/check")
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponseDTO> isInStock(@RequestParam List<String> skuCode) {
        return inventoryService.isInStock(skuCode);
    }
    
    @PutMapping("/quantity")
    public ResponseEntity<APIResponse<InventoryResponseDTO>> updateInventoryQuantity
            (
                    @RequestBody @Valid InventoryRequestDTO requestDTO
            ) throws InventoryNotFoundException {
        
        InventoryResponseDTO prodResponseDto = inventoryService.updateQuantity(requestDTO);
        
        APIResponse<InventoryResponseDTO> responseDTO = APIResponse
                .<InventoryResponseDTO>builder()
                .status(SUCCESS)
                .results(prodResponseDto)
                .build();
        
        log.info("InventoryController::updateInventoryQuantity response {}", Mapper.jsonToString(responseDTO));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
