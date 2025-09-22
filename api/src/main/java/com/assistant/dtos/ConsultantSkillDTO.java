package com.assistant.dtos;

import com.assistant.enums.SkillLevel;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ConsultantSkillDTO {
    private String technology;

    @JsonProperty("level")
    private SkillLevel level;

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public SkillLevel getLevel() {
        return level;
    }

    public void setLevel(SkillLevel level) {
        this.level = level;
    }
}
