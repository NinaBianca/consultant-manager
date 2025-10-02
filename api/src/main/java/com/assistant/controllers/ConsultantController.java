package com.assistant.controllers;

import com.assistant.dtos.*;
import com.assistant.entities.Consultant;
import com.assistant.entities.ConsultantSkill;
import com.assistant.entities.Skill;
import com.assistant.enums.SkillLevel;
import com.assistant.repositories.ConsultantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

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

    public List<ConsultantSkill> getSkillsByConsultantId(Long id) {
        return this.consultantRepository.findSkillsByConsultantId(id);
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

    public ConsultantResponseDTO convertToDTO(Consultant consultant) {
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

    public Consultant updateAvailabilityById(Long id, Boolean availability) {
        Optional<Consultant> optional = this.consultantRepository.findById(id);

        if(optional.isPresent()) {
            Consultant consultant = optional.get();
            consultant.setAvailable(availability);
            this.consultantRepository.save(consultant);
            return consultant;
        }

        return null;
    }

    public Boolean deleteConsultantById(Long id) {
        Optional<Consultant> optional = this.consultantRepository.findById(id);

        if(optional.isPresent()) {
            Consultant consultant = optional.get();
            this.consultantRepository.delete(consultant);
            return true;
        }

        return false;
    }

    public ConsultantResponseDTO updateConsultant(Long id, UpdateConsultantDTO dto) {
        Optional<Consultant> optional = this.consultantRepository.findById(id);

        if(optional.isPresent()) {
            Consultant consultant = optional.get();

            if (dto.getName() != null && !dto.getName().isEmpty()) {
                consultant.setName(dto.getName());
            }
            if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
                consultant.setEmail(dto.getEmail());
            }
            if (dto.getAvailable() != null) {
                consultant.setAvailable(dto.getAvailable());
            }
            if (dto.getConsultantType() != null) {
                consultant.setConsultantType(dto.getConsultantType());
            }
            Consultant updated = this.consultantRepository.save(consultant);
            return this.convertToDTO(updated);
        }

        return null;
    }

    public ConsultantSkillResponseDTO createSkillByConsultantId(Long id, ConsultantSkillDTO level) {
        Optional<Consultant> optional = this.consultantRepository.findById(id);

        if(optional.isPresent()) {
            Consultant consultant = optional.get();
            ConsultantSkill skill = new ConsultantSkill();
            Skill tech = this.skillController.findOrCreateSkill(level.getTechnology());
            skill.setSkill(tech);
            skill.setLevel(level.getLevel());
            consultant.getConsultantSkills().add(skill);
            Consultant updated = this.consultantRepository.save(consultant);

            ConsultantSkillResponseDTO responseDTO = ((LinkedList<ConsultantSkillResponseDTO>)this.convertToDTO(updated).getConsultantSkills()).getLast();
            return responseDTO;
        }

        return null;
    }

    public Boolean deleteSkillById(Long id, Long skillId) {
        Optional<Consultant> optional = this.consultantRepository.findById(id);

        if(optional.isPresent()) {
            Consultant consultant = optional.get();
            ConsultantSkill skill = ((LinkedList<ConsultantSkill>)consultant.getConsultantSkills().stream().filter(s -> s.getId() == skillId).toList()).getFirst();
            if (skill != null) {
                consultant.getConsultantSkills().remove(skill);
                Consultant updated = this.consultantRepository.save(consultant);
                if (!updated.getConsultantSkills().contains(skill)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<ConsultantResponseDTO> getConsultantBySkill(String technology, SkillLevel level) {
        List<SkillLevel> levels = new ArrayList<>();

        switch (level) {
            case BEGINNER -> {
                levels.add(SkillLevel.BEGINNER);
                levels.add(SkillLevel.INTERMEDIATE);
                levels.add(SkillLevel.EXPERT);
            }

            case INTERMEDIATE -> {
                levels.add(SkillLevel.INTERMEDIATE);
                levels.add(SkillLevel.EXPERT);
            }

            case EXPERT -> {
                levels.add(SkillLevel.EXPERT);
            }

        }

        List<Consultant> consultants = this.consultantRepository.findByMinimumSkillLevel(technology, levels);
        return consultants.stream().map( consultant -> this.convertToDTO(consultant)).toList();

    }
}
