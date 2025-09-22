package com.assistant.controllers;

import com.assistant.entities.Skill;
import com.assistant.repositories.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SkillController {
    @Autowired
    private final SkillRepository skillRepository;

    public SkillController(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
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
}
