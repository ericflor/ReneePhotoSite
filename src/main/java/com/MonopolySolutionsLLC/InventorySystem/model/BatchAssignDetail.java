package com.MonopolySolutionsLLC.InventorySystem.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchAssignDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "batch_assign_id", nullable = false)
    @JsonBackReference
    private BatchAssign batchAssign;

    private String imei;
    private boolean success;
}

