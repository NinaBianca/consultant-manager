package com.assistant.routes;

import com.assistant.controllers.ConsultantController;
import com.assistant.dtos.*;
import com.assistant.entities.Consultant;
import com.assistant.enums.SkillLevel;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.apache.camel.support.DefaultMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConsultantRouter extends RouteBuilder {
    @Autowired
    private final ConsultantController controller;

    public ConsultantRouter(ConsultantController controller) {
        this.controller = controller;
    }

    @Override
    public void configure() throws Exception {
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json);

        rest("/consultants/")
                .consumes("application/json").produces("application/json")
                .get().outType(ConsultantResponseDTO.class).to("direct:get-consultants")
                .get("/{id}").outType(ConsultantResponseDTO.class).to("direct:get-consultant-by-id")
                .get("/{id}/skills").outType(ConsultantSkillResponseDTO.class).to("direct:get-consultant-skills")
                .get("/available")
                .param().name("technology").type(RestParamType.query).description("tech").dataType("string").endParam()
                .param().name("level").type(RestParamType.query).description("level").dataType(SkillLevel.values().toString()).endParam()
                .to("direct:get-matching-consultants")

                .post().type(ConsultantDTO.class).outType(ConsultantResponseDTO.class).to("direct:add-consultant")
                .post("/{id}/skills").type(ConsultantSkillDTO.class).outType(ConsultantSkillResponseDTO.class).to("direct:update-skill-level")

                .put("/{id}").type(UpdateConsultantDTO.class).outType(ConsultantResponseDTO.class).to("direct:update-consultant")
                .put("/{id}/availability").type(Boolean.class).to("direct:update-availability")

                .delete("/{id}").to("direct:delete-consultant")
                .delete("/{id}/skills/{skillId}").to("direct:delete-skill");

        from("direct:get-consultants").process(this::getConsultants);
        from("direct:get-consultant-by-id").process(this::getConsultantById);
        from("direct:get-consultant-skills").process(this::getConsultantSkills);
        from("direct:get-matching-consultants").process(this::getMatchingConsultants);

        from("direct:add-consultant").process(this::addConsultant);
        from("direct:update-skill-level").process(this::updateSkillLevel);

        from("direct:update-consultant").process(this::updateConsultant);
        from("direct:update-availability").process(this::updateAvailability);

        from("direct:delete-consultant").process(this::deleteConsultant);
        from("direct:delete-skill").process(this::deleteSkill);
    }

    private void getMatchingConsultants(Exchange exchange) {
        String technology = exchange.getIn().getHeader("technology", String.class);
        SkillLevel level = exchange.getIn().getHeader("level", SkillLevel.class);

        if(technology == null || technology.isEmpty()) {
            log.error("Incoming technology value is null.");
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.BAD_REQUEST.value());
            exchange.getMessage().setBody("Request body is missing or invalid.");
            return;
        } else if(level == null) {
            log.error("Incoming level value is null.");
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.BAD_REQUEST.value());
            exchange.getMessage().setBody("Request body is missing or invalid.");
            return;
        }

        List<ConsultantResponseDTO> responseDTOS = this.controller.getConsultantBySkill(technology, level);
        if (responseDTOS == null) {
            responseDTOS = new ArrayList<>();
        }

        Message responseMessage = new DefaultMessage(exchange.getContext());
        responseMessage.setBody(responseDTOS);
        responseMessage.setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.OK.value());

        exchange.setMessage(responseMessage);
    }

    private void getConsultants(Exchange exchange) {
        List<Consultant> consultants = controller.getAllConsultants();

        if(consultants == null || consultants.isEmpty()) {
            log.error("No consultants found.");
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.BAD_REQUEST.value());
            exchange.getMessage().setBody("Request body is missing or invalid.");
            return;
        }

        List<ConsultantResponseDTO> responseDTOs = consultants.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Message responseMessage = new DefaultMessage(exchange.getContext());
        responseMessage.setBody(responseDTOs);
        responseMessage.setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.OK.value());

        exchange.setMessage(responseMessage);
    }

    private ConsultantResponseDTO convertToDTO(Consultant consultant) {
        return this.controller.convertToDTO(consultant);
    }

    private void getConsultantById(Exchange exchange) {
        Long id = exchange.getMessage().getHeader("id", Long.class);
        Consultant consultant = controller.getConsultantById(id);

        if(consultant == null) {
            log.error("Consultant not found with id: {}.", id.toString());
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.BAD_REQUEST.value());
            exchange.getMessage().setBody("Request body is missing or invalid.");
            return;
        }

        ConsultantResponseDTO responseDTO = this.convertToDTO(consultant);

        Message responseMessage = new DefaultMessage(exchange.getContext());
        responseMessage.setBody(responseDTO);
        responseMessage.setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.OK.value());

        exchange.setMessage(responseMessage);
    }

    private void getConsultantSkills(Exchange exchange) {
        Long id = exchange.getMessage().getHeader("id", Long.class);
        Consultant consultant = controller.getConsultantById(id);

        if(consultant == null) {
            log.error("Skills not found: Consultant not found with id: {}.", id.toString());
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.BAD_REQUEST.value());
            exchange.getMessage().setBody("Request body is missing or invalid.");
            return;
        }

        ConsultantResponseDTO dto = this.convertToDTO(consultant);
        List<ConsultantSkillResponseDTO> skills = dto.getConsultantSkills();

        if(skills == null) {
            log.error("Skills not found: No skills linked to consultant with id: {}.", id.toString());
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.BAD_REQUEST.value());
            exchange.getMessage().setBody("Request body is missing or invalid.");
            return;
        }

        Message responseMessage = new DefaultMessage(exchange.getContext());
        responseMessage.setBody(skills);
        responseMessage.setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.OK.value());

        exchange.setMessage(responseMessage);
    }

    private void addConsultant(Exchange exchange) {
        ConsultantDTO consultantDTO = exchange.getIn().getBody(ConsultantDTO.class);

        if (consultantDTO == null) {
            log.error("Incoming consultant entity is null.");
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.BAD_REQUEST.value());
            exchange.getMessage().setBody("Request body is missing or invalid.");
            return;
        }

        Consultant newConsultant = controller.addNewConsultant(consultantDTO);

        Message responseMessage = new DefaultMessage(exchange.getContext());
        responseMessage.setBody(newConsultant);
        responseMessage.setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.CREATED.value());

        exchange.setMessage(responseMessage);
    }

    private void updateSkillLevel(Exchange exchange) {
        ConsultantSkillDTO level = exchange.getIn().getBody(ConsultantSkillDTO.class);
        Long id = exchange.getMessage().getHeader("id", Long.class);

        if(id == null) {
            log.error("Incoming id value is null.");
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.BAD_REQUEST.value());
            exchange.getMessage().setBody("Request body is missing or invalid.");
            return;
        } else if(level == null) {
            log.error("Incoming level value is null.");
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.BAD_REQUEST.value());
            exchange.getMessage().setBody("Request body is missing or invalid.");
            return;
        }

        ConsultantSkillResponseDTO responseDTO = this.controller.createSkillByConsultantId(id, level);

        if (responseDTO == null) {
            log.error("Skill creation unsuccessful for consultant with id: {}.", id);
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.NOT_MODIFIED.value());
            exchange.getMessage().setBody("Skill creation unsuccessful.");
            return;
        }

        Message responseMessage = new DefaultMessage(exchange.getContext());
        responseMessage.setBody(responseDTO);
        responseMessage.setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.CREATED.value());

        exchange.setMessage(responseMessage);
    }

    private void updateConsultant(Exchange exchange) {
        UpdateConsultantDTO consultantDTO = exchange.getIn().getBody(UpdateConsultantDTO.class);
        Long id = exchange.getMessage().getHeader("id", Long.class);

        if(id == null) {
            log.error("Incoming id value is null.");
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.BAD_REQUEST.value());
            exchange.getMessage().setBody("Request body is missing or invalid.");
            return;
        } else if(consultantDTO == null) {
            log.error("Incoming consultant entity is null.");
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.BAD_REQUEST.value());
            exchange.getMessage().setBody("Request body is missing or invalid.");
            return;
        }

        ConsultantResponseDTO responseDTO = this.controller.updateConsultant(id, consultantDTO);

        if (responseDTO == null) {
            log.error("Update unsuccessful for consultant with id: {}.", id);
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.NOT_MODIFIED.value());
            exchange.getMessage().setBody("Update unsuccessful.");
            return;
        }

        Message responseMessage = new DefaultMessage(exchange.getContext());
        responseMessage.setBody(responseDTO);
        responseMessage.setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.CREATED.value());

        exchange.setMessage(responseMessage);
    }

    private void updateAvailability(Exchange exchange) {
        Boolean availability = exchange.getIn().getBody(Boolean.class);
        Long id = exchange.getMessage().getHeader("id", Long.class);

        if(id == null) {
            log.error("Incoming id value is null.");
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.BAD_REQUEST.value());
            exchange.getMessage().setBody("Request body is missing or invalid.");
            return;
        } else if(availability == null) {
            log.error("Incoming availability value is null.");
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.BAD_REQUEST.value());
            exchange.getMessage().setBody("Request body is missing or invalid.");
            return;
        }

        Consultant updatedConsultant = this.controller.updateAvailabilityById(id, availability);
        if(updatedConsultant != null) {
            Message responseMessage = new DefaultMessage(exchange.getContext());
            responseMessage.setBody(updatedConsultant);
            responseMessage.setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.CREATED.value());

            exchange.setMessage(responseMessage);
            return;
        }

        log.error("Availability update unsuccessful for consultant with id: {}.", id);
        exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.NOT_MODIFIED.value());
        exchange.getMessage().setBody("Availability update unsuccessful.");

    }

    private void deleteConsultant(Exchange exchange) {
        Long id = exchange.getMessage().getHeader("id", Long.class);

        if(id == null) {
            log.error("Incoming id value is null.");
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.BAD_REQUEST.value());
            exchange.getMessage().setBody("Request body is missing or invalid.");
            return;
        }

        Boolean success = this.controller.deleteConsultantById(id);

        if (success) {
            Message responseMessage = new DefaultMessage(exchange.getContext());
            responseMessage.setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.OK.value());

            exchange.setMessage(responseMessage);
            return;
        }

        log.error("Removal unsuccessful for consultant with id: {}.", id);
        exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.NOT_MODIFIED.value());
        exchange.getMessage().setBody("Consultant removal unsuccessful.");
    }

    private void deleteSkill(Exchange exchange) {
        Long id =  exchange.getMessage().getHeader("id", Long.class);
        Long skillId =  exchange.getMessage().getHeader("skillId", Long.class);

        if (id == null || skillId == null) {
            log.error("Incoming id or skillId value is null.");
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.BAD_REQUEST.value());
            exchange.getMessage().setBody("Request body is missing or invalid.");
            return;
        }

        Boolean success = this.controller.deleteSkillById(id, skillId);

        if (success) {
            Message responseMessage = new DefaultMessage(exchange.getContext());
            responseMessage.setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.OK.value());

            exchange.setMessage(responseMessage);
            return;
        }

        log.error("Skill removal unsuccessful for consultant with id: {}.", id);
        exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.NOT_MODIFIED.value());
        exchange.getMessage().setBody("Consultant skill removal unsuccessful.");
    }
}
