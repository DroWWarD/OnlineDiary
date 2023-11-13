package org.onlineDiary.repositories;

import org.onlineDiary.model.Squad;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudClassRepository extends CrudRepository<Squad, Long> {

    public Optional<Squad> findByName(String name);
}
