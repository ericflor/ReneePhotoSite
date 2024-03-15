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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> addMultipleOrders(@RequestBody List<Order> orders) {
        List<Order> savedOrders = ordersService.saveMultipleOrders(orders);
        return ResponseEntity.ok(savedOrders);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Order addOrder(@RequestBody Order order) {
        return ordersService.saveOrder(order);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Order>> getAllOrders(@PageableDefault() Pageable pageable) {
        Page<Order> orders = ordersService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public Optional<Order> getOrderById(@PathVariable Long id) {
        return ordersService.getOrderById(id);
    }

}
