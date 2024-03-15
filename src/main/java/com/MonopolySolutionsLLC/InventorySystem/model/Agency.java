package com.MonopolySolutionsLLC.InventorySystem.model;

import com.MonopolySolutionsLLC.InventorySystem.model.Enums.AgencyLevel;
import com.MonopolySolutionsLLC.InventorySystem.model.Enums.UserRole;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "agency")
public class Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String username;
    private String password;
    private Boolean blocked;

    @Enumerated(EnumType.STRING)
    private AgencyLevel level;

    @Enumerated(EnumType.STRING)
    private UserRole role;

}
