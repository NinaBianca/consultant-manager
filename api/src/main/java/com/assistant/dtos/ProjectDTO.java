package com.assistant.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ProjectDTO {

    @JsonProperty("projectDescription")
    private String projectDescription;

    @JsonProperty("requiredSkills")
    private List<ProjectSkillDTO> requiredSkills;

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public List<ProjectSkillDTO> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<ProjectSkillDTO> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
}