package com.MonopolySolutionsLLC.InventorySystem.model.DTOs;

import com.MonopolySolutionsLLC.InventorySystem.model.Phone;

public class UpdatePhoneResponse {
    private boolean success;
    private Phone phone;
    private String message;

    public UpdatePhoneResponse(boolean success, Phone phone, String message) {
        this.success = success;
        this.phone = phone;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

