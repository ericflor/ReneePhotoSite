package com.MonopolySolutionsLLC.InventorySystem.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String companyName;
    private String nameOfRecipient;
    private String phoneNumber;
    private String email;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String nameETC;
    private Integer quantity;
    private String notes;
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    private Status status;
}
