package com.MonopolySolutionsLLC.InventorySystem.service;

import com.MonopolySolutionsLLC.InventorySystem.exception.ResourceNotFoundException;
import com.MonopolySolutionsLLC.InventorySystem.model.Agency;
import com.MonopolySolutionsLLC.InventorySystem.model.UserRole;
import com.MonopolySolutionsLLC.InventorySystem.repo.AgencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgencyService {

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Agency saveAgency(Agency agency) {

        agency.setPassword(passwordEncoder.encode(agency.getPassword()));

        // Check if the level is 'employee', ignoring case
        if ("employee".equalsIgnoreCase(agency.getLevel().name())) {
            // If true, set the role of the agency to EMPLOYEE
            agency.setRole(UserRole.EMPLOYEE);
        }

        Agency existingAgency = agencyRepository.findByUsername(agency.getUsername());

        if (existingAgency != null) {
            throw new RuntimeException("The username: " + agency.getUsername() + " already exists.");
        }

        return agencyRepository.save(agency);
    }

    public List<Agency> getAllAgencies() {
        return agencyRepository.findAll();
    }

    public Agency getAgencyById(Long id) throws ResourceNotFoundException {
        return agencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found with ID: " + id));
    }

    public Agency updateAgency(Long id, Agency agencyDetails) throws ResourceNotFoundException {
        Agency agency = getAgencyById(id);
        agency.setName(agencyDetails.getName());
        agency.setEmail(agencyDetails.getEmail());
        agency.setUsername(agencyDetails.getUsername());
        agency.setPassword(agencyDetails.getPassword());
        agency.setLevel(agencyDetails.getLevel());
        return saveAgency(agency);
    }

    public void deleteAgency(Long id) {
        agencyRepository.deleteById(id);
    }
}
