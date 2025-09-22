package com.assistant.repositories;

import com.assistant.entities.Consultant;
import com.assistant.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConsultantRepository extends JpaRepository <Consultant, Long> {
    @Query("SELECT s FROM Skill s JOIN FETCH s.skills rs JOIN FETCH rs.skill")
    List<Consultant> findAllWithSkills();
}
