package com.assistant.entities;

import com.assistant.enums.ConsultantType;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "CONSULTANTS")
public class Consultant implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;

    @Column(name = "AVAILABLE")
    private Boolean available;

    @Enumerated(EnumType.STRING)
    private ConsultantType consultantType;

    @OneToMany(mappedBy = "consultant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ConsultantSkill> consultantSkills;

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

    public Set<ConsultantSkill> getConsultantSkills() {
        return consultantSkills;
    }

    public void setConsultantSkills(Set<ConsultantSkill> consultantSkills) {
        this.consultantSkills = consultantSkills;
    }
}