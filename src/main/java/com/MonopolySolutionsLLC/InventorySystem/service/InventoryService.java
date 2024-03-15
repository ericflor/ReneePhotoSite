package com.MonopolySolutionsLLC.InventorySystem.service;

import com.MonopolySolutionsLLC.InventorySystem.model.Phone;
import com.MonopolySolutionsLLC.InventorySystem.repo.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;


    public Phone savePhone(Phone phone) {
        return inventoryRepository.save(phone);
    }

    public List<Phone> savePhones(List<Phone> phones){
        return  inventoryRepository.saveAll(phones);
    }

    public Page<Phone> getAllPhones(Pageable pageable) {
        return inventoryRepository.findAll(pageable);
    }

    public Optional<Phone> getPhoneByImei(String imei) {
        return inventoryRepository.findByImei(imei);
    }

    public void deletePhone(String imei) {
        inventoryRepository.deleteByImei(imei);
    }

}
