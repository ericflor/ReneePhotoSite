package com.MonopolySolutionsLLC.InventorySystem.controller;

import com.MonopolySolutionsLLC.InventorySystem.exception.ResourceNotFoundException;
import com.MonopolySolutionsLLC.InventorySystem.model.Agency;
import com.MonopolySolutionsLLC.InventorySystem.model.Enums.AgencyLevel;
import com.MonopolySolutionsLLC.InventorySystem.service.AgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/agencies")
public class AgencyController {

    @Autowired
    private AgencyService agencyService;


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR', 'RETAILER')")
    public ResponseEntity<?> createAgency(@RequestBody Agency agency) {

        if (agency.getLevel() == null) {
            return ResponseEntity.badRequest().body("Error: Level is required.");
        }

        boolean levelExists = Arrays.stream(AgencyLevel.values())
                .anyMatch(e -> e.name().equalsIgnoreCase(agency.getLevel().name()));

        if (!levelExists) {
            return ResponseEntity.badRequest().body("Error: Invalid level value.");
        }

        return ResponseEntity.ok(agencyService.saveAgency(agency));
    }

    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR', 'RETAILER')")
    public ResponseEntity<List<Agency>> addMultipleAgencies(@RequestBody List<Agency> agencies) {
        if (agencies == null || agencies.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        List<Agency> savedAgencies = agencyService.saveMultipleAgencies(agencies);
        return ResponseEntity.ok(savedAgencies);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR', 'RETAILER', 'EMPLOYEE')")
    public ResponseEntity<Page<Agency>> getAllAgencies(@PageableDefault() Pageable pageable) {
        Page<Agency> agencies = agencyService.getAllAgencies(pageable);
        return ResponseEntity.ok(agencies);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR', 'RETAILER', 'EMPLOYEE')")
    public ResponseEntity<Agency> getAgencyById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(agencyService.getAgencyById(id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR', 'RETAILER')")
    public ResponseEntity<Agency> updateAgency(@PathVariable Long id, @RequestBody Agency agencyDetails) {
        try {
            return ResponseEntity.ok(agencyService.updateAgency(id, agencyDetails));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAgency(@PathVariable Long id) {
        try {
            agencyService.deleteAgency(id);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
