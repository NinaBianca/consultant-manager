package com.assistant.repositories;

import com.assistant.entities.Consultant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConsultantRepository extends JpaRepository <Consultant, Long> {
    @Query("SELECT c FROM Consultant c JOIN FETCH c.consultantSkills cs JOIN FETCH cs.skill")
    List<Consultant> findAllWithSkills();

    @Query("SELECT c FROM Consultant c JOIN FETCH c.consultantSkills cs JOIN FETCH cs.skill WHERE c.id = :id")
    Optional<Consultant> findByIdWithSkills(@Param("id") Long id);
}
