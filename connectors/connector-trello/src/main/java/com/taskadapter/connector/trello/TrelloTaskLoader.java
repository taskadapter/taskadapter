package com.taskadapter.connector.trello;

import com.julienvey.trello.Trello;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;

import java.util.List;
import java.util.stream.Collectors;

public class TrelloTaskLoader {

    private Trello api;

    public TrelloTaskLoader(Trello api) {
        this.api = api;
    }

    public List<GTask> loadTasks(TrelloConfig config) throws ConnectorException {
        try {
            var listsCache = new ListCache(api.getBoardLists(config.getBoardId()));
            var cards = api.getBoardCards(config.getBoardId());
            return cards.stream()
                    .map(c -> TrelloToGTask.convert(listsCache, c))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // TODO Trello process exceptions
            throw e;
        }
    }

    GTask loadTask(TrelloConfig config, String taskKey) {
        var listsCache = new ListCache(api.getBoardLists(config.getBoardId()));
        var card = api.getCard(taskKey);
        return TrelloToGTask.convert(listsCache, card);
    }
}
