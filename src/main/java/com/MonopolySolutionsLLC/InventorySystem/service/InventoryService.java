package com.MonopolySolutionsLLC.InventorySystem.service;

import com.MonopolySolutionsLLC.InventorySystem.exception.ResourceNotFoundException;
import com.MonopolySolutionsLLC.InventorySystem.model.Agency;
import com.MonopolySolutionsLLC.InventorySystem.model.Phone;
import com.MonopolySolutionsLLC.InventorySystem.repo.AgencyRepository;
import com.MonopolySolutionsLLC.InventorySystem.repo.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        return inventoryRepository.findAll(pageable);
    }

    public Optional<Phone> getPhoneByImei(String imei) {
        return inventoryRepository.findByImei(imei);
    }

    @Transactional
    public void deletePhone(String imei) {
        inventoryRepository.deleteByImei(imei);
    }

    public Phone updatePhone(String imei, Phone phoneDetails) {
        Phone phone = inventoryRepository.findByImei(imei)
                .orElseThrow(() -> new ResourceNotFoundException("Phone not found for this imei: " + imei));

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

        return inventoryRepository.save(phone);
    }
}
