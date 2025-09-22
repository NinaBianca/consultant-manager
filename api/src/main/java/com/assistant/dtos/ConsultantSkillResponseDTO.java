package com.assistant.dtos;

import com.assistant.enums.SkillLevel;

public class ConsultantSkillResponseDTO {
    private Long id;
    private String technology;
    private SkillLevel level;

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

    public SkillLevel getLevel() {
        return level;
    }

    public void setLevel(SkillLevel level) {
        this.level = level;
    }
}
