package com.MonopolySolutionsLLC.InventorySystem.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "phones")
public class Phone {

    @Id
    private String imei;

    private String status;
    private String type;
    private String model;
    private String masterAgent;
    private String distributor;
    private String retailer;
    private Date date;

    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency employee;
}
