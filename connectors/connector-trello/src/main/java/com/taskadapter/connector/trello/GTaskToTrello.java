package com.taskadapter.connector.trello;

import com.julienvey.trello.domain.Card;
import com.taskadapter.connector.common.data.ConnectorConverter;
import com.taskadapter.connector.definition.exception.FieldConversionException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.Children;
import com.taskadapter.model.Description;
import com.taskadapter.model.DueDate;
import com.taskadapter.model.Field;
import com.taskadapter.model.GTask;
import com.taskadapter.model.Id;
import com.taskadapter.model.Key;
import com.taskadapter.model.ParentKey;
import com.taskadapter.model.Relations;
import com.taskadapter.model.SourceSystemId;
import com.taskadapter.model.Summary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

public class GTaskToTrello implements ConnectorConverter<GTask, Card> {
    private static final Logger logger = LoggerFactory.getLogger(GTaskToTrello.class);

    private final TrelloConfig config;
    private final ListCache listCache;

    public GTaskToTrello(TrelloConfig config, ListCache listCache) {
        this.config = config;
        this.listCache = listCache;
    }

    public Card convert(GTask source) throws ConnectorException {
        var card = new Card();
        card.setId(source.getKey());
        card.setIdBoard(config.getBoardId());
        for (Map.Entry<Field<?>, Object> entry : source.getFields().entrySet()) {
            var field = entry.getKey();
            try {
                processField(card, field, entry.getValue());
            } catch (ConnectorException e) {
                throw e;
            } catch (Exception e) {
                throw new FieldConversionException(TrelloConnector.ID, entry.getKey(), entry.getValue(), e.getMessage());
            }
        }
        return card;
    }

    private void processField(Card card, Field<?> field, Object value) throws ConnectorException {
        if (field instanceof Children) {
            // processed in another place
            return;
        }
        if (field instanceof Id) { // ignore ID field because it does not need to be provided when saving
            return;
        }
        if (field instanceof Key) { // processed in <<DefaultValueSetter>>
            return;
        }
        if (field instanceof SourceSystemId) { // processed in <<DefaultValueSetter>>
            return;
        }
        if (field instanceof ParentKey) { // processed above
            return;
        }
        if (field instanceof Relations) { // processed in another place
            return;
        }

        if (field.equals(TrelloField.listId)) {
            card.setIdList((String) value);
            return;
        }
        if (field.equals(TrelloField.listName)) {
            var listName = (String) value;
            var listId = listCache.getListIdByName(listName);
            if (listId.isPresent()) {
                card.setIdList(listId.get());
            } else {
                throw new ConnectorException(
                        "Trello list with name '" + listName + "' is not found on the requested Trello Board (board ID " + config.getBoardId() + ")");
            }
        }
        if (field instanceof Summary) {
            card.setName((String) value);
            return;
        }
        if (field instanceof Description) {
            card.setDesc((String) value);
            return;
        }
        if (field instanceof DueDate) {
            card.setDue((Date) value);
            return;
        }
        logger.warn("Unknown field in GTask: " + field + ". Skipping it");
    }
}
