package com.assistant.repositories;

import com.assistant.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Project p JOIN FETCH p.requiredSkills rs JOIN FETCH rs.skill")
    List<Project> findAllWithSkills();
}