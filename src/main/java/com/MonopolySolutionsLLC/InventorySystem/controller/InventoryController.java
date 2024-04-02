package com.MonopolySolutionsLLC.InventorySystem.controller;

import com.MonopolySolutionsLLC.InventorySystem.exception.ResourceNotFoundException;
import com.MonopolySolutionsLLC.InventorySystem.model.DTOs.UpdatePhoneResponse;
import com.MonopolySolutionsLLC.InventorySystem.model.Phone;
import com.MonopolySolutionsLLC.InventorySystem.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR', 'RETAILER')")
    public ResponseEntity<Phone> addPhone(@RequestBody Phone phone) {
        return ResponseEntity.ok(inventoryService.savePhone(phone));
    }

    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR', 'RETAILER')")
    public ResponseEntity<List<Phone>> addPhonesBatch(@RequestBody List<Phone> phones) {
        return ResponseEntity.ok(inventoryService.savePhones(phones));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR', 'RETAILER', 'EMPLOYEE')")
    public ResponseEntity<Page<Phone>> getAllPhones(@PageableDefault() Pageable pageable) {
        Page<Phone> phones = inventoryService.getAllPhones(pageable);
        return ResponseEntity.ok(phones);
    }

    @GetMapping("/{imei}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR', 'RETAILER', 'EMPLOYEE')")
    public ResponseEntity<Optional<Phone>> getPhoneByIMEI(@PathVariable String imei) {
        try {
            return ResponseEntity.ok(inventoryService.getPhoneByImei(imei));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{imei}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR', 'RETAILER')")
    public ResponseEntity<?> updatePhone(@PathVariable String imei, @RequestBody Phone phoneDetails) {
        UpdatePhoneResponse response = inventoryService.updatePhone(imei, phoneDetails);
        if(response.isSuccess()) {
            return ResponseEntity.ok(response.getPhone());
        } else {
            return ResponseEntity.badRequest().body(response.getMessage());
        }
    }

//    @PatchMapping("/batch")
//    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR', 'RETAILER')")
//    public ResponseEntity<List<Phone>> updatePhonesBatch(@RequestBody List<Phone> phones) {
//        List<Phone> updatedPhones = new ArrayList<>();
//        for (Phone phone : phones) {
//            try {
//                Phone updatedPhone = inventoryService.updatePhone(phone.getImei(), phone);
//                updatedPhones.add(updatedPhone);
//            } catch (ResourceNotFoundException e) {
//                return ResponseEntity.notFound().build();
//            }
//        }
//        return ResponseEntity.ok(updatedPhones);
//    }

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