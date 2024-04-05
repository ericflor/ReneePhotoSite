package com.MonopolySolutionsLLC.InventorySystem.service;

import com.MonopolySolutionsLLC.InventorySystem.exception.ResourceNotFoundException;
import com.MonopolySolutionsLLC.InventorySystem.model.Agency;
import com.MonopolySolutionsLLC.InventorySystem.model.Enums.UserRole;
import com.MonopolySolutionsLLC.InventorySystem.repo.AgencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgencyService {

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public Agency saveAgency(Agency agency) {

        agency.setPassword(passwordEncoder.encode(agency.getPassword()));

        if ("employee".equalsIgnoreCase(agency.getLevel().name())) {
            agency.setRole(UserRole.EMPLOYEE);
        }
        else if ("distributor".equalsIgnoreCase(agency.getLevel().name())){
            agency.setRole(UserRole.DISTRIBUTOR);
        }
        else if ("retailer".equalsIgnoreCase(agency.getLevel().name())){
            agency.setRole(UserRole.RETAILER);
        }
        else {
            agency.setRole(UserRole.ADMIN);
        }

        Agency existingAgency = agencyRepository.findByUsername(agency.getUsername());

        if (existingAgency != null) {
            throw new RuntimeException("The username: " + agency.getUsername() + " already exists.");
        }

        return agencyRepository.save(agency);
    }

    public List<Agency> saveMultipleAgencies(List<Agency> agencies) {
        List<Agency> processedAgencies = agencies.stream().map(agency -> {
            agency.setPassword(passwordEncoder.encode(agency.getPassword()));
            if ("employee".equalsIgnoreCase(agency.getLevel().name())) {
                agency.setRole(UserRole.EMPLOYEE);
            }
            else if ("distributor".equalsIgnoreCase(agency.getLevel().name())){
                agency.setRole(UserRole.DISTRIBUTOR);
            }
            else if ("retailer".equalsIgnoreCase(agency.getLevel().name())){
                agency.setRole(UserRole.RETAILER);
            }
            else {
                agency.setRole(UserRole.ADMIN);
            }
            Agency existingAgency = agencyRepository.findByUsername(agency.getUsername());
            if (existingAgency != null) {
                throw new RuntimeException("The username: " + agency.getUsername() + " already exists.");
            }
            return agency;
        }).collect(Collectors.toList());

        return agencyRepository.saveAll(processedAgencies);
    }

    public Page<Agency> getAllAgencies(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return agencyRepository.findAll(pageable);
        } else {
            Agency agency = agencyRepository.findByUsername(currentUsername);
            if (agency != null) {
                List<Agency> agencies = List.of(agency);
                return new PageImpl<>(agencies, pageable, agencies.size());
            } else {
                return Page.empty(pageable);
            }
        }
    }

    public Agency getAgencyById(Long id) throws ResourceNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found with ID: " + id));

        if (isAdmin || agency.getUsername().equals(currentUsername)) {
            return agency;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    public Agency updateAgency(Long id, Agency agencyDetails) throws ResourceNotFoundException {
        Agency agency = getAgencyById(id);

        if (agency.getUsername().equalsIgnoreCase(agencyDetails.getUsername())) {
            throw new RuntimeException("The username: " + agency.getUsername() + " already exists.");
        }

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

        return agencyRepository.save(agency);
    }

    public void deleteAgency(Long id) {
        agencyRepository.deleteById(id);
    }
}
