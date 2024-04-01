package com.MonopolySolutionsLLC.InventorySystem.model.DTOs;

import com.MonopolySolutionsLLC.InventorySystem.model.BatchAssign;

import java.util.List;

public class BatchAssignCreateRequest {
    private BatchAssign batchAssign;
    private List<String> imeis;
    private List<Boolean> outcomes;

    public BatchAssign getBatchAssign() {
        return batchAssign;
    }

    public void setBatchAssign(BatchAssign batchAssign) {
        this.batchAssign = batchAssign;
    }

    public List<String> getImeis() {
        return imeis;
    }

    public void setImeis(List<String> imeis) {
        this.imeis = imeis;
    }

    public List<Boolean> getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(List<Boolean> outcomes) {
        this.outcomes = outcomes;
    }
}
