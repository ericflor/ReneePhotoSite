package com.MonopolySolutionsLLC.InventorySystem.controller;

import com.MonopolySolutionsLLC.InventorySystem.exception.ResourceNotFoundException;
import com.MonopolySolutionsLLC.InventorySystem.model.Phone;
import com.MonopolySolutionsLLC.InventorySystem.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<Phone>> getAllPhones() {
        return ResponseEntity.ok(inventoryService.getAllPhones());
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
            Phone existingPhone = inventoryService.getPhoneByImei(imei)
                    .orElseThrow(() -> new ResourceNotFoundException("Phone not found for this imei: " + imei));

            if (phoneDetails.getStatus() != null) existingPhone.setStatus(phoneDetails.getStatus());
            if (phoneDetails.getType() != null) existingPhone.setType(phoneDetails.getType());
            if (phoneDetails.getModel() != null) existingPhone.setModel(phoneDetails.getModel());
            if (phoneDetails.getMasterAgent() != null) existingPhone.setMasterAgent(phoneDetails.getMasterAgent());
            if (phoneDetails.getDistributor() != null) existingPhone.setDistributor(phoneDetails.getDistributor());
            if (phoneDetails.getRetailer() != null) existingPhone.setRetailer(phoneDetails.getRetailer());
            if (phoneDetails.getDate() != null) existingPhone.setDate(phoneDetails.getDate());

            Phone updatedPhone = inventoryService.savePhone(existingPhone);
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
