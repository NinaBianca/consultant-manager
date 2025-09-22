package com.assistant.controllers;

import com.assistant.dtos.ConsultantDTO;
import com.assistant.dtos.ConsultantSkillDTO;
import com.assistant.entities.Consultant;
import com.assistant.entities.ConsultantSkill;
import com.assistant.entities.Skill;
import com.assistant.repositories.ConsultantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class ConsultantController {
    @Autowired
    private final ConsultantRepository consultantRepository;

    @Autowired
    private final SkillController skillController;

    public ConsultantController(ConsultantRepository consultantRepository, SkillController skillController) {
        this.consultantRepository = consultantRepository;
        this.skillController = skillController;
    }

    public List<Consultant> getAllConsultants() { return this.consultantRepository.findAllWithSkills(); }

    public Consultant getConsultantById(Long id) {
        Optional<Consultant> consultant = this.consultantRepository.findByIdWithSkills(id);

        if(consultant.isPresent()) {
            return consultant.get();
        }
        return null;
    }

    public Consultant addNewConsultant(ConsultantDTO consultant) {
        Consultant newConsultant = new Consultant();
        newConsultant.setName(consultant.getName());
        newConsultant.setEmail(consultant.getEmail());
        newConsultant.setAvailable(consultant.getAvailable());
        newConsultant.setConsultantType(consultant.getConsultantType());

        Set<ConsultantSkill> consultantSkills = new HashSet<>();
        for (ConsultantSkillDTO skillDto : consultant.getConsultantSkills()) {

            Skill skill = skillController.findOrCreateSkill(skillDto.getTechnology());

            ConsultantSkill consultantSkill = new ConsultantSkill();
            consultantSkill.setConsultant(newConsultant);
            consultantSkill.setSkill(skill);
            consultantSkill.setLevel(skillDto.getLevel());

            consultantSkills.add(consultantSkill);
        }
        newConsultant.setConsultantSkills(consultantSkills);

        return consultantRepository.save(newConsultant);
    }

}
