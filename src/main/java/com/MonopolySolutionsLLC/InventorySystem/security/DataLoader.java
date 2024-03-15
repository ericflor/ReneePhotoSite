package com.MonopolySolutionsLLC.InventorySystem.security;

import com.MonopolySolutionsLLC.InventorySystem.model.Agency;
import com.MonopolySolutionsLLC.InventorySystem.model.Enums.UserRole;
import com.MonopolySolutionsLLC.InventorySystem.repo.AgencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final AgencyRepository agencyRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataLoader(AgencyRepository agencyRepository, PasswordEncoder passwordEncoder) {
        this.agencyRepository = agencyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Agency adminUser = Agency.builder()
                .username("admin")
                .password(passwordEncoder.encode("sl@123"))
                .role(UserRole.ADMIN)
                .build();

        if (agencyRepository.findByUsername(adminUser.getUsername()) == null) {
            agencyRepository.save(adminUser);
        }
    }
}
