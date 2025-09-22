package com.assistant.controllers;
import com.assistant.dtos.ProjectDTO;
import com.assistant.dtos.ProjectSkillDTO;
import com.assistant.entities.Project;
import com.assistant.entities.ProjectSkill;
import com.assistant.entities.Skill;
import com.assistant.repositories.ProjectRepository;
import com.assistant.repositories.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProjectController {

    @Autowired
    private final ProjectRepository projectRepository;

    @Autowired
    private final SkillController skillController;

    public ProjectController(ProjectRepository projectRepository, SkillController skillController) {
        this.projectRepository = projectRepository;
        this.skillController = skillController;
    }

    public Project addNewProject(ProjectDTO project) {
        Project newProject = new Project();
        newProject.setProjectDescription(project.getProjectDescription());

        Set<ProjectSkill> projectSkills = new HashSet<>();
        for (ProjectSkillDTO skillDto : project.getRequiredSkills()) {

            Skill skill = skillController.findOrCreateSkill(skillDto.getTechnology());

            ProjectSkill projectSkill = new ProjectSkill();
            projectSkill.setProject(newProject);
            projectSkill.setSkill(skill);
            projectSkill.setMinimumLevel(skillDto.getMinimumLevel());

            projectSkills.add(projectSkill);
        }
        newProject.setRequiredSkills(projectSkills);

        return projectRepository.save(newProject);
    }

    public List<Project> getAllProjects() {
        return this.projectRepository.findAllWithSkills();
    }

    public void deleteProject(Long id) throws Exception {
        Optional<Project> op = this.projectRepository.findById(id);
        if (op.isPresent()) {
            Project toDelete = op.get();
            this.projectRepository.delete(toDelete);
        } else {
            throw new Exception("Project not found");
        }
    }
}
