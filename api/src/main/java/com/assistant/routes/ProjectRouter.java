package com.assistant.routes;

import com.assistant.controllers.ProjectController;
import com.assistant.dtos.ProjectDTO;
import com.assistant.dtos.ProjectResponseDTO;
import com.assistant.dtos.ProjectSkillResponseDTO;
import com.assistant.entities.Project;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.support.DefaultMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ProjectRouter extends RouteBuilder {
    @Autowired
    final private ProjectController controller;

    public ProjectRouter(ProjectController controller) {
        this.controller = controller;
    }

    @Override
    public void configure() throws Exception {
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json);

        rest("/projects/")
                .consumes("application/json").produces("application/json")
                .get().outType(Project.class).to("direct:get-projects")
                .post().type(ProjectDTO.class).outType(Project.class).to("direct:add-project")
                .delete("/{id}").to("direct:delete-project");

        from("direct:get-projects").process(this::getProjects);

        from("direct:add-project").process(this::addProject);

        from("direct:delete-project").process(this::deleteProject);
    }

    private void getProjects(Exchange exchange) {
        List<Project> projects = controller.getAllProjects();

        List<ProjectResponseDTO> responseDTOs = projects.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Message responseMessage = new DefaultMessage(exchange.getContext());
        responseMessage.setBody(responseDTOs);
        responseMessage.setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.OK.value());

        exchange.setMessage(responseMessage);
    }

    private ProjectResponseDTO convertToDTO(Project project) {
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

    private void addProject(Exchange exchange) {
        ProjectDTO proj = exchange.getIn().getBody(ProjectDTO.class);

        if (proj == null) {
            log.error("Incoming project entity is null.");
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.BAD_REQUEST.value());
            exchange.getMessage().setBody("Request body is missing or invalid.");
            return;
        }

        Project newProject = controller.addNewProject(proj);

        Message responseMessage = new DefaultMessage(exchange.getContext());
        responseMessage.setBody(newProject);
        responseMessage.setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.CREATED.value());

        exchange.setMessage(responseMessage);
    }

    private void deleteProject(Exchange exchange) throws Exception {
        Long id = exchange.getMessage().getHeader("id", Long.class);
        controller.deleteProject(id);
    }
}