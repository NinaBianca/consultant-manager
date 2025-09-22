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
    private final SkillRepository skillRepository;

    public ProjectController(ProjectRepository projectRepository, SkillRepository skillRepository) {
        this.projectRepository = projectRepository;
        this.skillRepository = skillRepository;
    }

    public Project addNewProject(ProjectDTO project) {
        Project newProject = new Project();
        newProject.setProjectDescription(project.getProjectDescription());

        Set<ProjectSkill> projectSkills = new HashSet<>();
        for (ProjectSkillDTO skillDto : project.getRequiredSkills()) {

            Skill skill = findOrCreateSkill(skillDto.getTechnology());

            ProjectSkill projectSkill = new ProjectSkill();
            projectSkill.setProject(newProject);
            projectSkill.setSkill(skill);
            projectSkill.setMinimumLevel(skillDto.getMinimumLevel());

            projectSkills.add(projectSkill);
        }
        newProject.setRequiredSkills(projectSkills);

        return projectRepository.save(newProject);
    }

    public Skill findOrCreateSkill(String technology) {
        Optional<Skill> existingSkill = skillRepository.findByTechnology(technology);

        if (existingSkill.isPresent()) {
            return existingSkill.get();
        } else {
            Skill newSkill = new Skill();
            newSkill.setTechnology(technology);
            return skillRepository.save(newSkill);
        }
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
