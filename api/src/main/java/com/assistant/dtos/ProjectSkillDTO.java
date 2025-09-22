package com.assistant.dtos;

import com.assistant.enums.SkillLevel;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectSkillDTO {

    private String technology;

    @JsonProperty("minimumLevel")
    private SkillLevel minimumLevel;

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