package com.psajd.quizletBot.repositories;

import com.psajd.quizletBot.entities.CardPack;
import com.psajd.quizletBot.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardPackRepository extends JpaRepository<CardPack, Long> {
    Optional<CardPack> findByName(String name);
    @Transactional
    @Modifying
    @Query("update CardPack c set c.name = ?1, c.person = ?2 where c.id = ?3")
    int updateNameAndPersonById(String name, Person person, Long id);
    List<CardPack> findByPerson_Id(Long id);

    @Override
    void deleteById(Long aLong);
}
