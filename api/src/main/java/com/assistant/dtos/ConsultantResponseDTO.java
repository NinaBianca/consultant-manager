package com.assistant.dtos;

import com.assistant.enums.ConsultantType;

import java.util.List;
import java.util.Set;

public class ConsultantResponseDTO {
    private Long id;

    private String name;

    private String email;

    private Boolean available;

    private ConsultantType consultantType;

    private List<ConsultantSkillResponseDTO> consultantSkills;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public List<ConsultantSkillResponseDTO> getConsultantSkills() {
        return consultantSkills;
    }

    public void setConsultantSkills(List<ConsultantSkillResponseDTO> consultantSkills) {
        this.consultantSkills = consultantSkills;
    }
}
