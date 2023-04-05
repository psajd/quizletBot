package com.psajd.quizletBot.models.caching;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Getter
@Setter
public class TableCache {
    private final Map<Long, Integer> tableNumberMap = new HashMap<>();

    public void saveNumber(long userId, Integer number) {
        tableNumberMap.put(userId, number);
    }
}
