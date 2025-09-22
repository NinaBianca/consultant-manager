package com.assistant.dtos;

import com.assistant.enums.SkillLevel;

public class ProjectSkillResponseDTO {

    private Long id;
    private String technology;
    private SkillLevel minimumLevel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public SkillLevel getMinimumLevel() {
        return minimumLevel;
    }

    public void setMinimumLevel(SkillLevel minimumLevel) {
        this.minimumLevel = minimumLevel;
    }
}