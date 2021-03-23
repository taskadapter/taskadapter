package com.taskadapter.connector.trello;

import com.julienvey.trello.domain.Argument;
import com.julienvey.trello.domain.Card;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.GTask;

public class TrelloToGTask {
    static GTask convert(ListCache listCache, Card card) {
        var task = new GTask();

        var fakeEmptyId = 0L;
        var key = card.getId();
        task.setId(fakeEmptyId);
        task.setKey(key);
        // must set source system id, otherwise "update task" is impossible later
        task.setSourceSystemId(new TaskId(fakeEmptyId, key));

        task.setValue(AllFields.summary, card.getName());
        task.setValue(AllFields.dueDate, card.getDue());
        task.setValue(AllFields.updatedOn, card.getDateLastActivity());
        task.setValue(AllFields.description, card.getDesc());
        task.setValue(TrelloField.listId, card.getIdList());
        task.setValue(TrelloField.listName,
                listCache.getListNameById(card.getIdList()).orElse(""));
        // note - this sends a request to Trello server! Trello REST API limitations.
        var actions = card.getActions(new Argument("filter", "createCard"));
        if (!actions.isEmpty()) {
            var creator = actions.get(0).getMemberCreator();
            task.setValue(AllFields.reporterLoginName, creator.getUsername());
            task.setValue(AllFields.reporterFullName, creator.getFullName());
        }
        return task;
    }

}
