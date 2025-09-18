package com.consultancy.manager.repositories;

import com.consultancy.manager.entities.Consultant;
import org.springframework.data.repository.CrudRepository;

public interface ConsultantRepository extends CrudRepository <Consultant, Integer> {
}
