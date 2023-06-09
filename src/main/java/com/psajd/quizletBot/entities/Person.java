package com.psajd.quizletBot.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "person")
public class Person {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CardPack> cardPacks;

    public void addCardPack(CardPack cardPack) {
        cardPacks.add(cardPack);
        cardPack.setPerson(this);
    }

    public void removeCardPack(CardPack cardPack) {
        cardPacks.remove(cardPack);
        cardPack.setPerson(null);
    }
}
