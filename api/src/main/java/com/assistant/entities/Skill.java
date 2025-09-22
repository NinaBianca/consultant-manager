package com.assistant.entities;

import com.assistant.enums.SkillLevel;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name="SKILLS")
public class Skill implements Serializable {

    public Skill() {}

    @Id
    @GeneratedValue
    private Long id;

    @JsonProperty("technology")
    @Column(name = "TECHNOLOGY")
    private String technology;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTechnology() {
        return this.technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

}
