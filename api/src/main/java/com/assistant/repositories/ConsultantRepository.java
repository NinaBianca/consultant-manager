package com.assistant.repositories;

import com.assistant.entities.Consultant;
import com.assistant.entities.ConsultantSkill;
import com.assistant.enums.SkillLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConsultantRepository extends JpaRepository<Consultant, Long> {
    @Query("SELECT c FROM Consultant c JOIN FETCH c.consultantSkills cs JOIN FETCH cs.skill")
    List<Consultant> findAllWithSkills();

    @Query("SELECT c FROM Consultant c JOIN FETCH c.consultantSkills cs JOIN FETCH cs.skill WHERE c.id = :id")
    Optional<Consultant> findByIdWithSkills(@Param("id") Long id);

    @Query("SELECT cs FROM Consultant c JOIN c.consultantSkills cs JOIN cs.skill s WHERE c.id = :id")
    List<ConsultantSkill> findSkillsByConsultantId(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Consultant c JOIN c.consultantSkills cs JOIN cs.skill s WHERE s.technology = :technology AND cs.level IN :levels")
    List<Consultant> findByMinimumSkillLevel(@Param("technology") String technology, @Param("levels") List<SkillLevel> levels);
}
