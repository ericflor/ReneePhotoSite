package com.MonopolySolutionsLLC.InventorySystem.service;

import com.MonopolySolutionsLLC.InventorySystem.exception.ResourceNotFoundException;
import com.MonopolySolutionsLLC.InventorySystem.model.Agency;
import com.MonopolySolutionsLLC.InventorySystem.model.Enums.UserRole;
import com.MonopolySolutionsLLC.InventorySystem.repo.AgencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
            // Else, set the role of the agency to ADMIN
        } else {
            agency.setRole(UserRole.ADMIN);
        }

        Agency existingAgency = agencyRepository.findByUsername(agency.getUsername());

        if (existingAgency != null) {
            throw new RuntimeException("The username: " + agency.getUsername() + " already exists.");
        }

        return agencyRepository.save(agency);
    }

    public Page<Agency> getAllAgencies(Pageable pageable) {
        return agencyRepository.findAll(pageable);
    }

    public Agency getAgencyById(Long id) throws ResourceNotFoundException {
        return agencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found with ID: " + id));
    }

    public Agency updateAgency(Long id, Agency agencyDetails) throws ResourceNotFoundException {
        Agency agency = getAgencyById(id);

        if (agencyDetails.getName() != null) agency.setName(agencyDetails.getName());
        if (agencyDetails.getEmail() != null) agency.setEmail(agencyDetails.getEmail());
        if (agencyDetails.getUsername() != null) agency.setUsername(agencyDetails.getUsername());
        if (agencyDetails.getLevel() != null) agency.setLevel(agencyDetails.getLevel());
        if (agencyDetails.getBlocked() != null) {
            agency.setBlocked(agencyDetails.getBlocked());

            if (Boolean.TRUE.equals(agencyDetails.getBlocked())) {
                agency.setRole(UserRole.EMPLOYEE);
            } else {
                agency.setRole(UserRole.ADMIN);
            }
        }

        Agency existingAgency = agencyRepository.findByUsername(agency.getUsername());

        if (existingAgency != null) {
            throw new RuntimeException("The username: " + agency.getUsername() + " already exists.");
        }

        return agencyRepository.save(agency);
    }


    public void deleteAgency(Long id) {
        agencyRepository.deleteById(id);
    }
}
