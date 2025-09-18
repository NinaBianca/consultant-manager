package com.consultancy.manager.entities;

import com.consultancy.manager.enums.SkillLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="SKILLS")
public class Skill {

    @Column(name="TECHNOLOGY")
    private String technology;

    @Column(name="LEVEL")
    private SkillLevel level;

    public String getTechnology() {
        return this.technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public SkillLevel getLevel() {
        return this.level;
    }

    public void setLevel(SkillLevel level) {
        this.level = level;
    }
}
