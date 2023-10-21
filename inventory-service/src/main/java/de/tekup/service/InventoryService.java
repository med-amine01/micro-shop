package de.tekup.service;

import de.tekup.dto.InventoryRequestDTO;
import de.tekup.dto.InventoryResponseDTO;
import de.tekup.entity.Inventory;
import de.tekup.exception.InventoryAlreadyExistsException;
import de.tekup.exception.InventoryNotFoundException;
import de.tekup.exception.InventoryOutOfStockException;
import de.tekup.exception.InventoryServiceException;
import de.tekup.repository.InventoryRepository;
import de.tekup.util.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    

    public InventoryResponseDTO initQuantityFromQueue(String skuCode) throws Exception
    {
        try {
            // Initialize product with qte = 0
            // Read from RabbitMq when a new product is created and pushed to the queue
            
            if (inventoryRepository.existsBySkuCode(skuCode)) {
                throw new InventoryAlreadyExistsException("Cannot initialize, this product already exists in inventory");
            }
            
            Inventory inventory = new Inventory();
            inventory.setQuantity(0);
            inventory.setSkuCode(skuCode);
            
            return Mapper.toDto(inventoryRepository.save(inventory));

        } catch (InventoryAlreadyExistsException exception) {
            log.error("Exception occurred while initializing product, Exception message: {}", exception.getMessage());
            throw new InventoryAlreadyExistsException();
        } catch (Exception exception) {
            log.error("Exception occurred while fetching product from queue, Exception message: {}",exception.getMessage());
            throw new InventoryServiceException(exception.getMessage());
        }
    }
    
    public List<InventoryResponseDTO> getInventories() throws InventoryServiceException
    {
        try {
            List<Inventory> inventories = inventoryRepository.findAll();
            
            return inventories
                    .stream()
                    .map(Mapper::toDto)
                    .toList();

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new InventoryServiceException("Exception occurred while fetching inventories");
        }
    }
    
    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> isInStock(List<String> skuCode)
    {
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(inventory ->
                        InventoryResponseDTO.builder()
                                .skuCode(inventory.getSkuCode())
                                .isInStock(inventory.getQuantity() > 0)
                                .quantity(inventory.getQuantity())
                                .build()
                ).toList();
    }
    

    public InventoryResponseDTO updateQuantity(InventoryRequestDTO requestDTO, String skuCode) throws Exception
    {
        try {
            Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                    .orElseThrow(() -> new InventoryNotFoundException(skuCode + " not found"));
            
            if (requestDTO.isIncrease()) {
                inventory.increaseQte(requestDTO.getQuantity());
            } else {
                if (!isDiffQtePositive(requestDTO.getQuantity(), inventory.getQuantity())) {
                    throw new InventoryOutOfStockException("Not enough quantity in inventory");
                }
                inventory.decreaseQte(requestDTO.getQuantity());
            }
            
            return Mapper.toDto(inventoryRepository.save(inventory));
            
        }
        catch (InventoryOutOfStockException | InventoryNotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;
        }
        catch (Exception exception) {
            log.error("Exception occurred while updating quantity, Exception message: {}", exception.getMessage());
            throw new InventoryServiceException("Exception occurred while updating quantity");
        }
    }
    
    private static boolean isDiffQtePositive(int requestedQte, int inventoryQte)
    {
        int diffQte = inventoryQte - requestedQte;

        return diffQte >= 0;
    }
    
}
