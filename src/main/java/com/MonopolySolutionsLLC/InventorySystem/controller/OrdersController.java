package com.MonopolySolutionsLLC.InventorySystem.controller;

import com.MonopolySolutionsLLC.InventorySystem.model.Order;
import com.MonopolySolutionsLLC.InventorySystem.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Order updateOrder(@PathVariable Long id, @RequestBody Order order) {
        if (!order.getId().equals(id)) {
            throw new IllegalArgumentException("Order ID doesn't match URL ID");
        }
        return ordersService.saveOrder(order);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOrder(@PathVariable Long id) {
        ordersService.deleteOrder(id);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return ordersService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Optional<Order> getOrderById(@PathVariable Long id) {
        return ordersService.getOrderById(id);
    }

}
