package com.MonopolySolutionsLLC.InventorySystem.repo;

import com.MonopolySolutionsLLC.InventorySystem.model.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgencyRepository extends JpaRepository<Agency, Long> {

    Agency findByUsername(String username);

}
