package com.psajd.quizletBot.models.caching;

import com.psajd.quizletBot.entities.Card;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Setter
@Getter
public class CardCache {
    private final Map<Long, Card> cardMap = new HashMap<>();

    public void saveCard(long userId, Card card) {
        cardMap.put(userId, card);
    }
}