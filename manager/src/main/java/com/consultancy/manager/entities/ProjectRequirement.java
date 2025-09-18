package com.consultancy.manager.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="PROJECTS")
public class ProjectRequirement {
    @Id
    @GeneratedValue()
    private Integer id;

    @Column(name="DESCRIPTION")
    private String projectDescription;


    private List<Skill> skills;
}
