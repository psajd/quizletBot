package com.psajd.quizletBot.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "card_pack")
public class CardPack {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
    @OneToMany(mappedBy = "cardPack", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Card> cards = new HashSet<>();
    @ManyToOne
    private Person person;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CardPack cardPack = (CardPack) o;
        return getId() != null && Objects.equals(getId(), cardPack.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void addCard(Card card) {
        card.setCardPack(this);
        cards.add(card);
    }

    public void removeCard(Card card) {
        card.setCardPack(null);
        cards.remove(card);
    }
}
