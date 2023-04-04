package com.psajd.quizletBot.models.caching;

import com.psajd.quizletBot.entities.CardPack;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Setter
@Getter
public class CardPackCash {
    private final Map<Long, CardPack> cardPackMap = new HashMap<>();

    public void saveCardPack(long userId, CardPack cardPack) {
        cardPackMap.put(userId, cardPack);
    }
}
