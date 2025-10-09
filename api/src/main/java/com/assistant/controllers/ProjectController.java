package com.assistant.controllers;
import com.assistant.dtos.ProjectDTO;
import com.assistant.dtos.ProjectResponseDTO;
import com.assistant.dtos.ProjectSkillDTO;
import com.assistant.dtos.ProjectSkillResponseDTO;
import com.assistant.entities.Project;
import com.assistant.entities.ProjectSkill;
import com.assistant.entities.Skill;
import com.assistant.repositories.ProjectRepository;
import com.assistant.repositories.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ProjectController {

    @Autowired
    private final ProjectRepository projectRepository;

    @Autowired
    private final SkillController skillController;

    private final List<String> frontend;

    private final List<String> backend;

    public ProjectController(ProjectRepository projectRepository, SkillController skillController) {
        this.projectRepository = projectRepository;
        this.skillController = skillController;

        // FIXME static list of skills

        this.frontend = new ArrayList();
        frontend.add("REACT");
        frontend.add("VUE");
        frontend.add("ANGULAR");
        this.backend = new ArrayList<>();
        backend.add("JAVA");
        backend.add("SPRING");
        backend.add("CAMEL");

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

        Project added = projectRepository.save(newProject);

        return added;
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

    public ProjectResponseDTO convertToDTO(Project project) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(project.getId());
        dto.setProjectDescription(project.getProjectDescription());

        List<ProjectSkillResponseDTO> skillDTOs = project.getRequiredSkills().stream()
                .map(ps -> {
                    ProjectSkillResponseDTO skillDto = new ProjectSkillResponseDTO();
                    skillDto.setId(ps.getId());
                    skillDto.setTechnology(ps.getSkill().getTechnology());
                    skillDto.setMinimumLevel(ps.getMinimumLevel());
                    return skillDto;
                })
                .collect(Collectors.toList());

        dto.setRequiredSkills(skillDTOs);
        return dto;
    }
}
