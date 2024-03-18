package com.MonopolySolutionsLLC.InventorySystem.service;

import com.MonopolySolutionsLLC.InventorySystem.model.Order;
import com.MonopolySolutionsLLC.InventorySystem.exception.ResourceNotFoundException;
import com.MonopolySolutionsLLC.InventorySystem.repo.OrdersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OrdersService {

    @Autowired
    private OrdersRepository ordersRepository;



    public Order saveOrder(Order order) {

        order.setDate(new Date());

        return ordersRepository.save(order);
    }

    public List<Order> saveMultipleOrders(List<Order> orders) {

        orders.forEach(order -> order.setDate(new Date()));

        return ordersRepository.saveAll(orders);
    }

    public Page<Order> getAllOrders(Pageable pageable) {
        return ordersRepository.findAll(pageable);
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

    public Order updateOrder(Long id, Order orderDetails) {
        Order order = ordersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + id + " not found"));

        // Only update fields that are present in the request
        if (orderDetails.getCompanyName() != null) order.setCompanyName(orderDetails.getCompanyName());
        if (orderDetails.getNameOfRecipient() != null) order.setNameOfRecipient(orderDetails.getNameOfRecipient());
        if (orderDetails.getPhoneNumber() != null) order.setPhoneNumber(orderDetails.getPhoneNumber());
        if (orderDetails.getEmail() != null) order.setEmail(orderDetails.getEmail());
        if (orderDetails.getAddress() != null) order.setAddress(orderDetails.getAddress());
        if (orderDetails.getCity() != null) order.setCity(orderDetails.getCity());
        if (orderDetails.getState() != null) order.setState(orderDetails.getState());
        if (orderDetails.getZipCode() != null) order.setZipCode(orderDetails.getZipCode());
        if (orderDetails.getNameETC() != null) order.setNameETC(orderDetails.getNameETC());
        if (orderDetails.getQuantity() != null) order.setQuantity(orderDetails.getQuantity());
        if (orderDetails.getNotes() != null) order.setNotes(orderDetails.getNotes());
        if (orderDetails.getTrackingNumber() != null) order.setTrackingNumber(orderDetails.getTrackingNumber());
        if (orderDetails.getStatus() != null) order.setStatus(orderDetails.getStatus());

        return ordersRepository.save(order);
    }
}
