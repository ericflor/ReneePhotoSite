package com.MonopolySolutionsLLC.InventorySystem.controller;

import com.MonopolySolutionsLLC.InventorySystem.model.BatchAssign;
import com.MonopolySolutionsLLC.InventorySystem.model.DTOs.BatchAssignCreateRequest;
import com.MonopolySolutionsLLC.InventorySystem.service.BatchAssignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/assign")
public class BatchAssignController {

    @Autowired
    private BatchAssignService batchAssignService;

    @PostMapping
    public ResponseEntity<BatchAssign> createBatchAssign(@RequestBody BatchAssignCreateRequest request) {
        BatchAssign createdBatchAssign = batchAssignService.createBatchAssign(
                request.getBatchAssign(),
                request.getImeis(),
                request.getOutcomes());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBatchAssign);
    }

    @GetMapping
    public ResponseEntity<Page<BatchAssign>> getAllBatchAssigns(Pageable pageable) {
        Page<BatchAssign> batchAssigns = batchAssignService.getAllBatchAssigns(pageable);
        return ResponseEntity.ok(batchAssigns);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatchAssign> getBatchAssignById(@PathVariable Long id) {
        Optional<BatchAssign> batchAssign = batchAssignService.getBatchAssignById(id);
        return batchAssign.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
