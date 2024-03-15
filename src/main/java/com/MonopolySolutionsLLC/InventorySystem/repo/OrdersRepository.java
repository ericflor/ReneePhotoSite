package com.MonopolySolutionsLLC.InventorySystem.repo;

import com.MonopolySolutionsLLC.InventorySystem.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Long> {
}
