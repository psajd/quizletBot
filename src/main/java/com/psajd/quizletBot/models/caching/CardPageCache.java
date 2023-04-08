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
public class CardPageCache {
    private final Map<Long, Integer> cardPageMap = new HashMap<>();

    public void saveCardPage(long userId, Integer num) {
        cardPageMap.put(userId, num);
    }
}