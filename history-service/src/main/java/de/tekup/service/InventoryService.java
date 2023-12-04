package de.tekup.service;

import de.tekup.entity.Inventory;
import de.tekup.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    
    public List<Inventory> findAllInventories() {
        return inventoryRepository.findAll();
    }
    
    public Inventory findInventoryById(Long id) {
        return inventoryRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("inventory with id " + id + " not found")
        );
    }
    
    public Inventory saveInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }
}
