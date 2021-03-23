package com.taskadapter.connector.trello;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import com.taskadapter.connector.common.BasicIssueSaveAPI;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.exceptions.ConnectorException;

public class TrelloTaskSaver implements BasicIssueSaveAPI<Card> {
    private final Trello api;

    public TrelloTaskSaver(Trello api) {
        this.api = api;
    }

    @Override
    public TaskId createTask(Card nativeTask) throws ConnectorException {
        var newCard = api.createCard(nativeTask.getIdList(), nativeTask);
        long longId = 0;
        return new TaskId(longId, newCard.getId() + "");
    }

    @Override
    public void updateTask(Card nativeTask) throws ConnectorException {
        api.updateCard(nativeTask);
    }
}
