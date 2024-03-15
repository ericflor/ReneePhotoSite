package com.MonopolySolutionsLLC.InventorySystem.integration;

import com.MonopolySolutionsLLC.InventorySystem.model.Agency;
import com.MonopolySolutionsLLC.InventorySystem.model.Enums.AgencyLevel;
import com.MonopolySolutionsLLC.InventorySystem.model.Enums.UserRole;
import com.MonopolySolutionsLLC.InventorySystem.model.Phone;
import com.MonopolySolutionsLLC.InventorySystem.repo.AgencyRepository;
import com.MonopolySolutionsLLC.InventorySystem.repo.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private AgencyRepository agencyRepository;

    @BeforeEach
    public void setup() {
        inventoryRepository.deleteAll();
        agencyRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdatePhoneEmployee() throws Exception {
        // Create an Agency (Employee)
        Agency employee = new Agency();
        employee.setName("John Doe");
        employee.setEmail("john@doe.com");
        employee.setUsername("johndoe");
        employee.setPassword("password");
        employee.setBlocked(false);
        employee.setLevel(AgencyLevel.EMPLOYEE);
        employee.setRole(UserRole.EMPLOYEE);
        Agency savedEmployee = agencyRepository.save(employee);

        System.out.println("EMPLOYEE ID: " + savedEmployee.getId());

        // Create a phone
        Phone phone = new Phone();
        phone.setImei("123456789012345");
        phone.setStatus("Available");
        phone.setType("Smartphone");
        phone.setModel("Model X");
        phone.setMasterAgent("MasterAgent");
        phone.setDistributor("Distributor");
        phone.setRetailer("Retailer");
        phone.setDate(new Date());
        phone.setEmployee(null); // Initially, no employee is assigned
        Phone savedPhone = inventoryRepository.save(phone);

        // Prepare JSON to update the phone's employee
        String jsonPatch = String.format("{ \"employee\": { \"id\": \"%s\" } }", savedEmployee.getId());

        // Perform PATCH request to update the phone's assigned employee
        mockMvc.perform(patch("/inventory/" + savedPhone.getImei())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPatch))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imei").value(savedPhone.getImei()))
                .andExpect(jsonPath("$.employee.id").value(savedEmployee.getId()));
    }

    private Agency createTestAgency() {
        Agency agency = new Agency();
        agency.setName("Test Agency");
        agency.setEmail("testagency@example.com");
        agency.setUsername("testagency");
        agency.setPassword("testpass");
        agency.setBlocked(false);
        agency.setLevel(AgencyLevel.EMPLOYEE);
        agency.setRole(UserRole.EMPLOYEE);
        return agencyRepository.save(agency);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAddNewPhone() throws Exception {
        String newPhoneJson = """
        {
            "imei": "987654321098765",
            "status": "Available",
            "type": "Smartphone",
            "model": "Galaxy S10",
            "masterAgent": "MasterAgent1",
            "distributor": "Distributor1",
            "retailer": "Retailer1",
            "date": "2023-03-15T12:00:00.000+00:00"
        }
        """;

        mockMvc.perform(post("/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newPhoneJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imei").value("987654321098765"))
                .andExpect(jsonPath("$.status").value("Available"))
                .andExpect(jsonPath("$.type").value("Smartphone"))
                .andExpect(jsonPath("$.model").value("Galaxy S10"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdatePhoneStatus() throws Exception {
        // Pre-create a phone in the system
        Agency employee = createTestAgency();
        Phone phone = new Phone("123456789012345", "Available", "Smartphone", "Model X", "MasterAgent", "Distributor", "Retailer", new Date(), employee);
        Phone savedPhone = inventoryRepository.save(phone);

        // JSON to update phone status
        String updateJson = "{\"status\":\"Sold\"}";

        mockMvc.perform(patch("/inventory/" + savedPhone.getImei())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Sold"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeletePhone() throws Exception {
        // Pre-create a phone in the system
        Agency employee = createTestAgency();
        Phone phone = new Phone("123456789012346", "Available", "Smartphone", "Model Y", "MasterAgent2", "Distributor2", "Retailer2", new Date(), employee);
        Phone savedPhone = inventoryRepository.save(phone);

        mockMvc.perform(delete("/inventory/" + savedPhone.getImei()))
                .andExpect(status().isOk());

        // Verify phone is deleted
        Optional<Phone> foundPhone = inventoryRepository.findByImei(savedPhone.getImei());
        assertFalse(foundPhone.isPresent());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdatePhoneModel() throws Exception {
        Agency employee = createTestAgency();
        Phone phone = new Phone("987654321098764", "Available", "Smartphone", "Model 1", "MasterAgent", "Distributor", "Retailer", new Date(), employee);
        inventoryRepository.save(phone);

        String updateJson = "{\"model\":\"Model 2\"}";

        mockMvc.perform(patch("/inventory/" + phone.getImei())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Model 2"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAddPhonesBatch() throws Exception {
        String bulkPhonesJson = """
        [
            {
                "imei": "111111111111111",
                "status": "Available",
                "type": "Smartphone",
                "model": "Model A",
                "masterAgent": "MasterAgentA",
                "distributor": "DistributorA",
                "retailer": "RetailerA",
                "date": "2023-03-15T12:00:00.000+00:00"
            },
            {
                "imei": "222222222222222",
                "status": "Available",
                "type": "Tablet",
                "model": "Model B",
                "masterAgent": "MasterAgentB",
                "distributor": "DistributorB",
                "retailer": "RetailerB",
                "date": "2023-03-16T12:00:00.000+00:00"
            }
        ]
    """;

        mockMvc.perform(post("/inventory/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bulkPhonesJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].imei").value("111111111111111"))
                .andExpect(jsonPath("$.[1].imei").value("222222222222222"));
    }
}
