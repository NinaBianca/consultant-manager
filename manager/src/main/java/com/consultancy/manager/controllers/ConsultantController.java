package com.consultancy.manager.controllers;

import com.consultancy.manager.entities.Consultant;
import com.consultancy.manager.entities.Skill;
import com.consultancy.manager.enums.SkillLevel;
import com.consultancy.manager.repositories.ConsultantRepository;

import java.util.List;

public class ConsultantController {
    final ConsultantRepository consultantRepo;

    public ConsultantController(ConsultantRepository repository) {
        this.consultantRepo = repository;
    }

    // POST /consultants - Nieuw consultant profiel aanmaken
    public Consultant addNewConsultant(Consultant consultant) {
        return null;
    }

    // PUT /consultants/{id} - Consultant informatie bijwerken
    public Consultant updateConsultant(Integer id, Consultant consultant) {
        return null;
    }

    // PUT /consultants/{id}/availability - Consultant beschikbaarheid
    public Consultant updateConsultantAvailability(Integer id, Boolean availability) {
        return null;
    }

    // GET /consultants/{id} - Consultant profiel ophalen inclusief vaardigheden
    public Consultant getConsultantFullProfile(Integer id) {
        return null;
    }

    // DELETE /consultants/{id} - Consultant profiel verwijderen
    public void deleteConsultant(Integer id){

    }

    // GET /consultants - Alle consultants weergeven
    public List<Consultant> getConsultants(){
        return null;
    }

    // POST /consultants/{id}/skills - Vaardighedsniveau van consultant bijwerken voor een technologie
    public Skill updateSkillLevel(Integer id, Skill skill) {
        return null;
    }

    // GET /consultants/{id}/skills - Vaardigheden ophalen
    public List<Skill> getSkillsByConsultantId(Integer id) {
        return null;
    }

    // DELETE /consultants/{id}/skills?technology={tech} - Vaardighedsniveau van consultant verwijderen
    public void deleteConsultantSkillByTechnology(Integer id, String technology) {

    }

    // GET /consultants/available?technology={tech}&level={level} - Beschikbare consultants zoeken
    public List<Consultant> getAvailableConsultantsBySkill(String technology, SkillLevel level) {
        return null;
    }
}
