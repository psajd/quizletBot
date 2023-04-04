package com.psajd.quizletBot.repositories;

import com.psajd.quizletBot.entities.CardPack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardPackRepository extends JpaRepository<CardPack, Long> {
    List<CardPack> findByPerson_Id(Long id);
}
