package com.assistant.dtos;

import java.util.List;

public class ProjectResponseDTO {

    private Long id;
    private String projectDescription;
    private List<ProjectSkillResponseDTO> requiredSkills;

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

    public List<ProjectSkillResponseDTO> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<ProjectSkillResponseDTO> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
}