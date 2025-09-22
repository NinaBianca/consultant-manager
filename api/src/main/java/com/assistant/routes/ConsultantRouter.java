package com.assistant.routes;

import com.assistant.controllers.ConsultantController;
import com.assistant.dtos.*;
import com.assistant.entities.Consultant;
import com.assistant.entities.Project;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.support.DefaultMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConsultantRouter extends RouteBuilder {
    @Autowired
    private final ConsultantController controller;

    public ConsultantRouter(ConsultantController controller) {
        this.controller = controller;
    }


//   TODO
//    DELETE /consultants/{id}/skills?technology={tech} - Vaardighedsniveau van consultant verwijderen
//    GET /consultants/available?technology={tech}&level={level} - Beschikbare consultants zoeken

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

                .post().type(ConsultantDTO.class).outType(ConsultantResponseDTO.class).to("direct:add-consultant")
                .post("/{id}/skills").type(ConsultantSkillDTO.class).outType(ConsultantSkillResponseDTO.class).to("direct:update-skill-level")

                .put("/{id}").type(ConsultantDTO.class).to("direct:update-consultant")
                .put("/{id}/availability").type(Boolean.class).to("direct:update-availability")

                .delete("/{id}").to("direct:delete-consultant");

        from("direct:get-consultants").process(this::getConsultants);
        from("direct:get-consultant-by-id").process(this::getConsultantById);
        from("direct:get-consultant-skills").process(this::getConsultantSkills);

        from("direct:add-consultant").process(this::addConsultant);
        from("direct:update-skill-level").process(this::updateSkillLevel);

        from("direct:update-consultant").process(this::updateConsultant);
        from("direct:update-availability").process(this::updateAvailability);

        from("direct:delete-consultant").process(this::deleteConsultant);
    }

    private void getConsultants(Exchange exchange) {
        List<Consultant> consultants = controller.getAllConsultants();

        List<ConsultantResponseDTO> responseDTOs = consultants.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Message responseMessage = new DefaultMessage(exchange.getContext());
        responseMessage.setBody(responseDTOs);
        responseMessage.setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.OK.value());

        exchange.setMessage(responseMessage);
    }

    private ConsultantResponseDTO convertToDTO(Consultant consultant) {
        ConsultantResponseDTO dto = new ConsultantResponseDTO();
        dto.setId(consultant.getId());
        dto.setName(consultant.getName());
        dto.setEmail(consultant.getEmail());
        dto.setAvailable(consultant.getAvailable());
        dto.setConsultantType(consultant.getConsultantType());

        List<ConsultantSkillResponseDTO> skillDTOs = consultant.getConsultantSkills().stream()
                .map(ps -> {
                    ConsultantSkillResponseDTO skillDto = new ConsultantSkillResponseDTO();
                    skillDto.setId(ps.getId());
                    skillDto.setTechnology(ps.getSkill().getTechnology());
                    skillDto.setLevel(ps.getLevel());
                    return skillDto;
                })
                .collect(Collectors.toList());

        dto.setConsultantSkills(skillDTOs);
        return dto;
    }

    public void getConsultantById(Exchange exchange) {
        // consultant with skills
    }

    public void getConsultantSkills(Exchange exchange) {

    }

    private void addConsultant(Exchange exchange) {
    }

    public void updateSkillLevel(Exchange exchange) {

    }

    public void updateConsultant(Exchange exchange) {

    }

    public void updateAvailability(Exchange exchange) {

    }

    private void deleteConsultant(Exchange exchange) {
    }
}
