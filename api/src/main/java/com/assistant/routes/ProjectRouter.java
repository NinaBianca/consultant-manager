package com.assistant.routes;

import com.assistant.controllers.ProjectController;
import com.assistant.dtos.ProjectDTO;
import com.assistant.dtos.ProjectResponseDTO;
import com.assistant.entities.Project;
import com.slack.api.model.block.Blocks;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.support.DefaultMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProjectRouter extends RouteBuilder {

    private ProjectController controller;

    @Value("${slack.channel.frontend}")
    private String frontendChannel;

    @Value("${slack.channel.backend}")
    private String backendChannel;

    @Value("${slack.channel.general}")
    private String generalChannel;

    public static final String NOTIFICATION_TYPE_HEADER = "ProjectNotificationType";
    public static final String FRONTEND_TYPE = "Frontend";
    public static final String BACKEND_TYPE = "Backend";
    public static final String GENERAL_TYPE = "General";
    public static final String SLACK_CHANNEL_NAME_HEADER = "SlackChannelName";

    private final Set<String> FRONTEND_SKILLS = Set.of("REACT", "ANGULAR", "VUE", "JAVASCRIPT", "TYPESCRIPT", "HTML", "CSS");
    private final Set<String> BACKEND_SKILLS  = Set.of("JAVA", "SPRING BOOT", "PYTHON", "DJANGO", "GOLANG", "NODE.JS", "C#");

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
                .post().type(ProjectDTO.class).outType(Project.class).to("direct:save-and-route-project")
                .delete("/{id}").to("direct:delete-project");

        from("direct:get-projects").process(this::getProjects);

        from("direct:delete-project").process(this::deleteProject);

        from("direct:save-and-route-project")
                .process(this::addProject)
                .process(this::setNotificationRouteHeader)
                .to("direct:notification-router");

        from("direct:notification-router")
                .routeId("notificationRouter")
                .choice()

                .when(header("NeedsFrontendNotification").isEqualTo(true))
                .setHeader(NOTIFICATION_TYPE_HEADER, constant(FRONTEND_TYPE))
                .setHeader(SLACK_CHANNEL_NAME_HEADER, constant(frontendChannel))
                .to("direct:prepare-and-notify")

                .when(header("NeedsBackendNotification").isEqualTo(true))
                .setHeader(NOTIFICATION_TYPE_HEADER, constant(BACKEND_TYPE))
                .setHeader(SLACK_CHANNEL_NAME_HEADER, constant(backendChannel))
                .to("direct:prepare-and-notify")

                .otherwise()
                .setHeader(NOTIFICATION_TYPE_HEADER, constant(GENERAL_TYPE))
                .setHeader(SLACK_CHANNEL_NAME_HEADER, constant(generalChannel))
                .to("direct:prepare-and-notify")
                .end();

        from("direct:prepare-and-notify")
                .log("Preparing Slack message for ${header.ProjectNotificationType} channel.")
                .process(this::prepareSlackMessage)
                .toD("slack:${header.SlackChannelName}")
                .log("Slack notification process initiated successfully to channel: ${header.SlackChannelName}");
    }

    public void setNotificationRouteHeader(Exchange exchange) {
        Project addedProject = exchange.getIn().getBody(Project.class);

        if (addedProject == null) {
            exchange.getIn().setHeader("NeedsFrontendNotification", false);
            exchange.getIn().setHeader("NeedsBackendNotification", false);
            return;
        }

        Set<String> projectTechnologies = addedProject.getRequiredSkills().stream()
                .map(ps -> ps.getSkill().getTechnology().toUpperCase())
                .collect(Collectors.toSet());

        boolean needsFrontend = projectTechnologies.stream()
                .anyMatch(FRONTEND_SKILLS::contains);

        boolean needsBackend = projectTechnologies.stream()
                .anyMatch(BACKEND_SKILLS::contains);

        exchange.getIn().setHeader("NeedsFrontendNotification", needsFrontend);
        exchange.getIn().setHeader("NeedsBackendNotification", needsBackend);
    }

    public void prepareSlackMessage(Exchange exchange) {
        Project project = exchange.getIn().getBody(Project.class);
        String projectType = exchange.getIn().getHeader(NOTIFICATION_TYPE_HEADER, GENERAL_TYPE, String.class);

        if (project == null) {
            log.warn("Project entity is null when preparing Slack message.");
            return;
        }

        String titleText = String.format("*New %s Project Added:*", projectType);
        String descriptionText = project.getProjectDescription();

        String skills = project.getRequiredSkills().stream()
                .map(ps -> ps.getSkill().getTechnology())
                .collect(Collectors.joining(", "));
        String skillsText = String.format("*Required Skills:* %s", skills);


        List<LayoutBlock> blocks = Arrays.asList(
                // Title Section Block
                SectionBlock.builder()
                        .text(MarkdownTextObject.builder()
                                .text(titleText)
                                .build())
                        .build(),

                // Description Section Block
                SectionBlock.builder()
                        .text(MarkdownTextObject.builder()
                                .text(descriptionText)
                                .build())
                        .build(),

                // Divider
                Blocks.divider(),

                // Skills Section Block
                SectionBlock.builder()
                        .text(MarkdownTextObject.builder()
                                .text(skillsText)
                                .build())
                        .build()
        );

        com.slack.api.model.Message slackMessage = new com.slack.api.model.Message();
        slackMessage.setBlocks(blocks);

        exchange.getIn().setBody(slackMessage);
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
        return this.controller.convertToDTO(project);
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