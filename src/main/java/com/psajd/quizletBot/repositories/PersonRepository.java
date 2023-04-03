package com.psajd.quizletBot.repositories;

import com.psajd.quizletBot.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    @Override
    Optional<Person> findById(Long aLong);
}
