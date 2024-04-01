package com.MonopolySolutionsLLC.InventorySystem.service;

import com.MonopolySolutionsLLC.InventorySystem.exception.CustomDatabaseException;
import com.MonopolySolutionsLLC.InventorySystem.model.BatchAssign;
import com.MonopolySolutionsLLC.InventorySystem.model.BatchAssignDetail;
import com.MonopolySolutionsLLC.InventorySystem.repo.BatchAssignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BatchAssignService {

    @Autowired
    private BatchAssignRepository batchAssignRepository;


    public BatchAssign createBatchAssign(BatchAssign batchAssign, List<String> imeis, List<Boolean> outcomes) {
        String uniquePart = UUID.randomUUID().toString().split("-")[0];
        String timestampPart = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String batchCode = "BA-" + timestampPart + "-" + uniquePart;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        for (int i = 0; i < imeis.size(); i++) {
            BatchAssignDetail detail = new BatchAssignDetail();
            detail.setBatchAssign(batchAssign);
            detail.setImei(imeis.get(i));
            detail.setSuccess(outcomes.get(i));
            batchAssign.getDetails().add(detail);
        }

        batchAssign.setBatchCode(batchCode);
        batchAssign.setUploadedOn(new Date());
        batchAssign.setUploadedBy(username);

        try {
            return batchAssignRepository.save(batchAssign);
        } catch (Exception e) {
            throw new CustomDatabaseException("Error saving BatchAssign", e);
        }
    }

    public Page<BatchAssign> getAllBatchAssigns(Pageable pageable) {
        return batchAssignRepository.findAll(pageable);
    }

    public Optional<BatchAssign> getBatchAssignById(Long id) {
        return batchAssignRepository.findById(id);
    }

}
