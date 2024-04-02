package com.MonopolySolutionsLLC.InventorySystem.repo;

import com.MonopolySolutionsLLC.InventorySystem.model.BatchAssign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchAssignRepository extends JpaRepository<BatchAssign, Long> {
//    Page<BatchAssign> findByUploadedByUsername(String username, Pageable pageable);x

}
