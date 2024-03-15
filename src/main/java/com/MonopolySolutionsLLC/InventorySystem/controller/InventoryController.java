package com.MonopolySolutionsLLC.InventorySystem.controller;

import com.MonopolySolutionsLLC.InventorySystem.exception.ResourceNotFoundException;
import com.MonopolySolutionsLLC.InventorySystem.model.Phone;
import com.MonopolySolutionsLLC.InventorySystem.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Phone> addPhone(@RequestBody Phone phone) {
        return ResponseEntity.ok(inventoryService.savePhone(phone));
    }

    @PostMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Phone>> addPhonesBatch(@RequestBody List<Phone> phones){
        return  ResponseEntity.ok(inventoryService.savePhones(phones));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Phone>> getAllPhones(@PageableDefault() Pageable pageable) {
        Page<Phone> phones = inventoryService.getAllPhones(pageable);
        return ResponseEntity.ok(phones);
    }

    @GetMapping("/{imei}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Optional<Phone>> getPhoneByIMEI(@PathVariable String imei) {
        try {
            return ResponseEntity.ok(inventoryService.getPhoneByImei(imei));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{imei}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Phone> updatePhone(@PathVariable String imei, @RequestBody Phone phoneDetails) {
        try {
            Phone updatedPhone = inventoryService.updatePhone(imei, phoneDetails);
            return ResponseEntity.ok(updatedPhone);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{imei}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePhone(@PathVariable String imei) {
        try {
            inventoryService.deletePhone(imei);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
