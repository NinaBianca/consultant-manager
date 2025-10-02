package com.assistant.entities;

import com.assistant.enums.SkillLevel;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "CONSULTANT_SKILLS")
public class ConsultantSkill implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "consultant_id", nullable = false)
    private Consultant consultant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(name = "LEVEL", nullable = false)
    private SkillLevel level;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setConsultant(Consultant consultant) {
        this.consultant = consultant;
    }

    public Consultant getConsultant() {
        return consultant;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setLevel(SkillLevel level) {
        this.level = level;
    }

    public SkillLevel getLevel() {
        return level;
    }
}