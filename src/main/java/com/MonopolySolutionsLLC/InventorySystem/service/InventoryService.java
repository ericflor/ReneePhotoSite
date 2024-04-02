package com.MonopolySolutionsLLC.InventorySystem.service;

import com.MonopolySolutionsLLC.InventorySystem.exception.ResourceNotFoundException;
import com.MonopolySolutionsLLC.InventorySystem.model.Agency;
import com.MonopolySolutionsLLC.InventorySystem.model.DTOs.UpdatePhoneResponse;
import com.MonopolySolutionsLLC.InventorySystem.model.Enums.UserRole;
import com.MonopolySolutionsLLC.InventorySystem.model.Phone;
import com.MonopolySolutionsLLC.InventorySystem.repo.AgencyRepository;
import com.MonopolySolutionsLLC.InventorySystem.repo.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private AgencyRepository agencyRepository;



    public Phone savePhone(Phone phone) {
        phone.setDate(new Date());
        return inventoryRepository.save(phone);
    }

    public List<Phone> savePhones(List<Phone> phones){
        phones.forEach(phone -> phone.setDate(new Date()));
        return  inventoryRepository.saveAll(phones);
    }

    public Page<Phone> getAllPhones(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<Agency> agencyOptional = Optional.ofNullable(agencyRepository.findByUsername(username));

        // Check the user's role to determine the appropriate data access strategy
        if (agencyOptional.isPresent() && agencyOptional.get().getRole().equals(UserRole.ADMIN)) {
            // ADMIN sees everything
            return inventoryRepository.findAll(pageable);
        } else {
            // Other roles see only their associated records
            return inventoryRepository.findByEmployee_Username(username, pageable);
        }
    }

    public Optional<Phone> getPhoneByImei(String imei) {
        // Adjust to fetch based on user's role and association
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Agency currentUserAgency = agencyRepository.findByUsername(username);

        if (currentUserAgency.getRole().equals(UserRole.ADMIN)) {
            // ADMIN can access any phone
            return inventoryRepository.findByImei(imei);
        } else {
            // Other roles can access only their phones
            return inventoryRepository.findByImeiAndEmployee_Username(imei, username);
        }
    }

    @Transactional
    public void deletePhone(String imei) throws ResourceNotFoundException {
        boolean exists = inventoryRepository.existsByImei(imei);
        if (!exists) {
            throw new ResourceNotFoundException("Phone not found for this imei: " + imei);
        }
        inventoryRepository.deleteByImei(imei);
    }

    public UpdatePhoneResponse updatePhone(String imei, Phone phoneDetails) {

        try {
                Phone phone = inventoryRepository.findByImei(imei)
                        .orElseThrow(() -> new ResourceNotFoundException("Phone not found for this imei: " + imei));

                // Fetch the current user's agency information based on username
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String username = authentication.getName();
                Agency currentUserAgency = agencyRepository.findByUsername(username);
                if (currentUserAgency == null) {
                    throw new EntityNotFoundException("Current user not found");
                }

                // Logic to determine if the current user can perform the update based on their role
                boolean isUpdateAllowed = false;

                switch (currentUserAgency.getRole()) {
                    case ADMIN:
                        // ADMIN can transfer anywhere
                        isUpdateAllowed = true;
                        break;
                    case DISTRIBUTOR:
                        // DISTRIBUTOR can transfer to RETAILER and EMPLOYEE
                        if (phoneDetails.getMasterAgent() == null) {
                            isUpdateAllowed = true;
                        }
                        break;
                    case RETAILER:
                        // RETAILER can transfer to EMPLOYEE
                        if (phoneDetails.getMasterAgent() == null && phoneDetails.getDistributor() == null) {
                            isUpdateAllowed = true;
                        }
                        break;
                    case EMPLOYEE:
                        // EMPLOYEE can't use the system for transferring
                        isUpdateAllowed = false;
                        break;
                }

                if (!isUpdateAllowed) {
                    throw new AccessDeniedException("You do not have permission to perform this action.");
                }

                // Update fields if they are not null
                if (phoneDetails.getStatus() != null) phone.setStatus(phoneDetails.getStatus());
                if (phoneDetails.getType() != null) phone.setType(phoneDetails.getType());
                if (phoneDetails.getModel() != null) phone.setModel(phoneDetails.getModel());
                if (phoneDetails.getMasterAgent() != null) phone.setMasterAgent(phoneDetails.getMasterAgent());
                if (phoneDetails.getDistributor() != null) phone.setDistributor(phoneDetails.getDistributor());
                if (phoneDetails.getRetailer() != null) phone.setRetailer(phoneDetails.getRetailer());
                if (phoneDetails.getDate() != null) phone.setDate(phoneDetails.getDate());


                if (phoneDetails.getEmployee() != null && phoneDetails.getEmployee().getId() != null) {
                    Agency existingAgency = agencyRepository.findById(phoneDetails.getEmployee().getId()).orElseThrow(() -> new EntityNotFoundException("Agency not found"));
                    phone.setEmployee(existingAgency);
                }

            Phone updatedPhone = inventoryRepository.save(phone);
            return new UpdatePhoneResponse(true, updatedPhone, "Phone updated successfully");

        } catch (ResourceNotFoundException | EntityNotFoundException | AccessDeniedException e) {
            return new UpdatePhoneResponse(false, null, e.getMessage());
        }
    }

}
