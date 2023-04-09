package com.psajd.quizletBot.models.handlers;

import com.psajd.quizletBot.constants.BotCommands;
import com.psajd.quizletBot.constants.BotMessages;
import com.psajd.quizletBot.entities.Card;
import com.psajd.quizletBot.entities.CardPack;
import com.psajd.quizletBot.models.BotState;
import com.psajd.quizletBot.models.ReplyKeyboardFactory;
import com.psajd.quizletBot.models.caching.*;
import com.psajd.quizletBot.services.ServiceAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

@Component
public class ShowCardsHandler {
    private final int cardsAmount = 3;
    private CardPackCache cardPackCache;
    private CardPageCache cardPageCache;
    private BotStateCache botStateCache;
    private MainMenuEventsHandler mainMenuEventsHandler;

    public BotApiMethod<?> showCards(Long chatId, Message message) {
        CardPack cardPack = cardPackCache
                .getCardPackMap()
                .get(chatId);

        if (cardPack.getCards().isEmpty()) {
            cardPageCache.saveCardPage(chatId, 1);
            botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
            return mainMenuEventsHandler.getPackInfo(chatId, cardPackCache.getCardPackMap().get(chatId).getName());
        }

        if (cardPageCache.getCardPageMap().get(chatId) == null) {
            cardPageCache.saveCardPage(chatId, 1);
        }

        BotApiMethod<?> method = chooseNextPageOrGoBack(chatId, message);
        if (method != null) {
            return method;
        }

        List<Card> cards = cardPack.getCards().stream().toList();
        int pageIndex = cardPageCache.getCardPageMap().get(chatId) - 1;

        SendMessage sendMessage = new SendMessage(chatId.toString(), BotMessages.INFO_YOUR_CARDS.getAnswer());
        sendMessage.setReplyMarkup(ReplyKeyboardFactory.onShowCards(cards, pageIndex + 1));
        mainMenuEventsHandler.executeAdditionalMethod(sendMessage);

        return new SendMessage(chatId.toString(),
                getResultString(cards, pageIndex));
    }

    private BotApiMethod<?> chooseNextPageOrGoBack(Long chatId, Message message) {
        if (!message.getText().equals(BotCommands.SHOW_CARDS.getCommand())) {
            if (message.getText().equals("⬅️")) {
                cardPageCache.saveCardPage(chatId, cardPageCache.getCardPageMap().get(chatId) - 1);
            } else if (message.getText().equals("➡️")) {
                cardPageCache.saveCardPage(chatId, cardPageCache.getCardPageMap().get(chatId) + 1);
            } else if (message.getText().equals(BotCommands.GO_BACK.getCommand())) {
                cardPageCache.saveCardPage(chatId, 1);
                botStateCache.saveBotState(chatId, BotState.ON_PACK_INFO);
                return mainMenuEventsHandler.getPackInfo(chatId, cardPackCache.getCardPackMap().get(chatId).getName());
            } else {
                return new SendMessage(message.getChatId().toString(), BotMessages.EXCEPTION_TRY_AGAIN.getAnswer());
            }
        }
        return null;
    }

    private String getResultString(List<Card> cards, int pageIndex) {
        List<String> resultList = new ArrayList<>();
        for (int i = 0; i < cardsAmount; i++) {
            Card card = cards.size() - (pageIndex * 3 + i) > 0 ? cards.get(pageIndex * 3 + i) : null;
            if (card != null) {
                String s = card.getTerm() + " - " + card.getDefinition();
                resultList.add(s);
            }
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < resultList.size(); i++) {
            String s = resultList.get(i);
            result.append("*****\n");
            result.append(pageIndex * 3 + i + 1).append(". ");
            result.append(s);
            result.append("\n*****\n\n");
        }
        return result.toString();
    }

    @Autowired
    public void setCardPackCache(CardPackCache cardPackCache) {
        this.cardPackCache = cardPackCache;
    }

    @Autowired
    public void setCardPageCache(CardPageCache cardPageCache) {
        this.cardPageCache = cardPageCache;
    }

    @Autowired
    public void setBotStateCache(BotStateCache botStateCache) {
        this.botStateCache = botStateCache;
    }

    @Autowired
    public void setMainMenuEventsHandler(MainMenuEventsHandler mainMenuEventsHandler) {
        this.mainMenuEventsHandler = mainMenuEventsHandler;
    }
}
