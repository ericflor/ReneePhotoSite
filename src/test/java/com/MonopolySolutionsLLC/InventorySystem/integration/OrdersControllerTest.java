package com.MonopolySolutionsLLC.InventorySystem.integration;

import com.MonopolySolutionsLLC.InventorySystem.model.Enums.Status;
import com.MonopolySolutionsLLC.InventorySystem.model.Order;
import com.MonopolySolutionsLLC.InventorySystem.repo.OrdersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrdersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrdersRepository ordersRepository;

    @BeforeEach
    public void setup() {
        ordersRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAddOrder() throws Exception {
        String newOrderJson = """
            {
                "companyName": "Acme Inc.",
                "nameOfRecipient": "John Doe",
                "phoneNumber": "1234567890",
                "email": "john.doe@example.com",
                "address": "123 Main St",
                "city": "Anytown",
                "state": "Anystate",
                "zipCode": "12345",
                "quantity": 10,
                "status": "NEW"
            }
        """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOrderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("Acme Inc."))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateOrderStatus() throws Exception {
        // Create an order to update
        Order order = new Order(null, "Test Inc", "Jane Doe", "0987654321", "jane.doe@example.com", "456 Elm St", "Yettown", "Yesstate", "54321", "ETC", 5, "Urgent", "12345", new Date(), Status.NEW);
        Order savedOrder = ordersRepository.save(order);

        String updatedOrderJson = "{\"status\":\"APPROVED\"}";

        mockMvc.perform(patch("/orders/" + savedOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedOrderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeleteOrder() throws Exception {
        // Create an order to delete
        Order order = new Order(null, "Delete Me Inc", "Doomed User", "1231231234", "doomed@example.com", "789 Doom St", "Doomtown", "Doomstate", "66666", "DOOM", 666, "Very Urgent", "666", new Date(), Status.NEW);
        Order savedOrder = ordersRepository.save(order);

        mockMvc.perform(delete("/orders/" + savedOrder.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/orders/" + savedOrder.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetAllOrders() throws Exception {

        mockMvc.perform(get("/orders").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0)); // Expecting no orders if database is empty
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetOrderById() throws Exception {
        // Create an order to fetch
        Order order = new Order(null, "Fetch Me Inc", "Fetchy User", "3213214321", "fetchy@example.com", "321 Fetch St", "Fetchtown", "Fetchstate", "33333", "FETCH", 333, "Mildly Urgent", "333", new Date(), Status.NEW);
        Order savedOrder = ordersRepository.save(order);

        mockMvc.perform(get("/orders/" + savedOrder.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("Fetch Me Inc"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateNonExistentOrder() throws Exception {
        String updateJson = "{\"status\":\"DENIED\"}";

        mockMvc.perform(patch("/orders/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeleteNonExistentOrder() throws Exception {
        mockMvc.perform(delete("/orders/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddOrderWithoutBeingAdmin() throws Exception {
        String newOrderJson = "{}"; // Empty JSON object to simulate missing data

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOrderJson))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals("Access is denied", result.getResolvedException().getMessage()));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testBulkAddOrders() throws Exception {
        String bulkOrderJson = """
        [
            {
                "companyName": "Bulk Company 1",
                "nameOfRecipient": "Bulk Recipient 1",
                "phoneNumber": "1111111111",
                "email": "bulk1@example.com",
                "address": "123 Bulk St",
                "city": "Bulk City",
                "state": "Bulk State",
                "zipCode": "11111",
                "quantity": 5,
                "status": "NEW"
            },
            {
                "companyName": "Bulk Company 2",
                "nameOfRecipient": "Bulk Recipient 2",
                "phoneNumber": "2222222222",
                "email": "bulk2@example.com",
                "address": "456 Bulk St",
                "city": "Bulk City",
                "state": "Bulk State",
                "zipCode": "22222",
                "quantity": 10,
                "status": "NEW"
            }
        ]
    """;

        mockMvc.perform(post("/orders/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bulkOrderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].companyName").value("Bulk Company 1"))
                .andExpect(jsonPath("$.[1].companyName").value("Bulk Company 2"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateOrderToShipped() throws Exception {
        // Create an order
        Order order = new Order(null, "Company Name", "John Doe", "123456789", "john.doe@example.com", "123 Main St", "City", "State", "12345", null, 10, null, null, new Date(), Status.APPROVED);
        Order savedOrder = ordersRepository.save(order);

        // Update the order status to SHIPPED
        mockMvc.perform(patch("/orders/" + savedOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"SHIPPED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.SHIPPED.name()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateOrderToDenied() throws Exception {
        // Create an order
        Order order = new Order(null, "Company Name", "Jane Doe", "987654321", "jane.doe@example.com", "456 Another St", "Another City", "Another State", "54321", null, 5, null, null, new Date(), Status.NEW);
        Order savedOrder = ordersRepository.save(order);

        // Update the order status to DENIED
        mockMvc.perform(patch("/orders/" + savedOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"DENIED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.DENIED.name()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetOrderByIdSuccess() throws Exception {
        // Create and save an order
        Order newOrder = new Order(null, "Test Company", "John Smith", "555-555-5555", "john@test.com", "123 Test Ave", "Test City", "TS", "12345", "Test Order", 1, "No notes", null, new Date(), Status.NEW);
        Order savedOrder = ordersRepository.save(newOrder);

        // Perform the GET request
        mockMvc.perform(get("/orders/" + savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId()))
                .andExpect(jsonPath("$.companyName").value("Test Company"))
                .andExpect(jsonPath("$.status").value(Status.NEW.toString()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetAllOrdersPaginated() throws Exception {
        // Assume orders have been added to the database in the setup

        // Perform the GET request for the first page with a specific page size
        mockMvc.perform(get("/orders").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").isNumber())
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.totalPages").isNumber());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetNonExistentOrderById() throws Exception {
        // Attempt to fetch an order that does not exist
        mockMvc.perform(get("/orders/9999999999")) // Using an ID that is unlikely to exist
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetOrdersPaginatedEmptyResult() throws Exception {
        // Fetching a page that doesn't exist (assuming a clean database or a very high page number)
        mockMvc.perform(get("/orders").param("page", "100").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

}
