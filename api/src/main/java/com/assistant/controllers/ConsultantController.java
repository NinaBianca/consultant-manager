package com.assistant.controllers;

import com.assistant.entities.Consultant;
import com.assistant.repositories.ConsultantRepository;
import com.assistant.repositories.ProjectRepository;
import com.assistant.repositories.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConsultantController {
    @Autowired
    private final ConsultantRepository consultantRepository;

    @Autowired
    private final SkillRepository skillRepository;

    public ConsultantController(ConsultantRepository consultantRepository, SkillRepository skillRepository) {
        this.consultantRepository = consultantRepository;
        this.skillRepository = skillRepository;
    }

    public List<Consultant> getAllConsultants() { return this.consultantRepository.findAllWithSkills(); }


}
