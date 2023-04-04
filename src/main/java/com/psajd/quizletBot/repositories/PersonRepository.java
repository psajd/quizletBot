package com.psajd.quizletBot.repositories;

import com.psajd.quizletBot.entities.CardPack;
import com.psajd.quizletBot.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    @Transactional
    @Modifying
    @Query("update Person p set p.name = ?1, p.cardPacks = ?2 where p.id = ?3")
    int updateNameAndCardPacksById(String name, CardPack cardPacks, Long id);
    @Override
    Optional<Person> findById(Long aLong);
}
