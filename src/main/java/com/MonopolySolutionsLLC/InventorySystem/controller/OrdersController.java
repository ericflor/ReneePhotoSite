package com.MonopolySolutionsLLC.InventorySystem.controller;

import com.MonopolySolutionsLLC.InventorySystem.exception.ResourceNotFoundException;
import com.MonopolySolutionsLLC.InventorySystem.model.Order;
import com.MonopolySolutionsLLC.InventorySystem.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;


    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR', 'RETAILER')")
    public ResponseEntity<List<Order>> addMultipleOrders(@RequestBody List<Order> orders) {
        List<Order> savedOrders = ordersService.saveMultipleOrders(orders);
        return ResponseEntity.ok(savedOrders);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR', 'RETAILER')")
    public Order addOrder(@RequestBody Order order) {
        return ordersService.saveOrder(order);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR', 'RETAILER')")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order orderDetails) {
        try {
            Order updatedOrder = ordersService.updateOrder(id, orderDetails);
            return ResponseEntity.ok(updatedOrder);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOrder(@PathVariable Long id) {
        ordersService.deleteOrder(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR', 'RETAILER', 'EMPLOYEE')")
    public ResponseEntity<Page<Order>> getAllOrders(@PageableDefault() Pageable pageable) {
        Page<Order> orders = ordersService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR', 'RETAILER', 'EMPLOYEE')")
    public Optional<Order> getOrderById(@PathVariable Long id) {
        return ordersService.getOrderById(id);
    }

}
