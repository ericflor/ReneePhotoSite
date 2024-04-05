package com.MonopolySolutionsLLC.InventorySystem.repo;

import com.MonopolySolutionsLLC.InventorySystem.model.Phone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Phone, String> {

    Optional<Phone> findByImei(String imei);

    boolean existsByImei(String imei);

    void deleteByImei(String imei);

    Page<Phone> findByEmployee_Username(String username, Pageable pageable);
    Optional<Phone> findByImeiAndEmployee_Username(String imei, String username);


}
