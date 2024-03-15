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
        Order order = new Order(null, "Test Inc", "Jane Doe", "0987654321", "jane.doe@example.com", "456 Elm St", "Yettown", "Yesstate", "54321", "ETC", 5, "Urgent", "12345", Status.NEW);
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
        Order order = new Order(null, "Delete Me Inc", "Doomed User", "1231231234", "doomed@example.com", "789 Doom St", "Doomtown", "Doomstate", "66666", "DOOM", 666, "Very Urgent", "666", Status.NEW);
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
        Order order = new Order(null, "Fetch Me Inc", "Fetchy User", "3213214321", "fetchy@example.com", "321 Fetch St", "Fetchtown", "Fetchstate", "33333", "FETCH", 333, "Mildly Urgent", "333", Status.NEW);
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
}
