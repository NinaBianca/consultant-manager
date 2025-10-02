package com.assistant.dtos;

import com.assistant.enums.ConsultantType;

import java.util.Set;

public class UpdateConsultantDTO {

    private String name;

    private String email;

    private Boolean available;

    private ConsultantType consultantType;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public ConsultantType getConsultantType() {
        return consultantType;
    }

    public void setConsultantType(ConsultantType consultantType) {
        this.consultantType = consultantType;
    }

}
