package com.assistant.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="PROJECTS")
public class Project implements Serializable {

    public Project () {}

    @Id
    @GeneratedValue
    private Long id;

    @JsonProperty("projectDescription")
    @Column(name="DESCRIPTION")
    private String projectDescription;

    @JsonProperty("requiredSkills")
    @JsonManagedReference
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectSkill> requiredSkills = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public Set<ProjectSkill> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(Set<ProjectSkill> skills) {
        this.requiredSkills = skills;
    }
}
