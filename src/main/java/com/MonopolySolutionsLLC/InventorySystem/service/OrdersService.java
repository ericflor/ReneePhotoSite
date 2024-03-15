package com.MonopolySolutionsLLC.InventorySystem.service;

import com.MonopolySolutionsLLC.InventorySystem.model.Order;
import com.MonopolySolutionsLLC.InventorySystem.exception.ResourceNotFoundException;
import com.MonopolySolutionsLLC.InventorySystem.repo.OrdersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OrdersService {

    @Autowired
    private OrdersRepository ordersRepository;



    public Order saveOrder(Order order) {
        return ordersRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return ordersRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return Optional.ofNullable(ordersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + id + " not found")));
    }

    public void deleteOrder(Long id) {
        if (!ordersRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete, order with ID " + id + " not found");
        }
        ordersRepository.deleteById(id);
    }

    public List<Order> saveMultipleOrders(List<Order> orders) {
        return ordersRepository.saveAll(orders);
    }
}
