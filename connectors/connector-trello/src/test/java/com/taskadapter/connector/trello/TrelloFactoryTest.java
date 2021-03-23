package com.taskadapter.connector.trello;

import com.taskadapter.model.AllFields;
import com.taskadapter.model.Field;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TrelloFactoryTest {
    private static final List<Field<?>> defaultFieldsForNewConfig = new TrelloFactory().getDefaultFieldsForNewConfig();

    @Test
    public void defaultFieldsHaveTrelloListName() {
        assertThat(defaultFieldsForNewConfig).contains(TrelloField.listName);
    }

    @Test
    public void defaultFieldsDoNotHaveThese() {
        assertThat(defaultFieldsForNewConfig).doesNotContain(TrelloField.listId, AllFields.reporterFullName,
                AllFields.reporterLoginName, AllFields.id, AllFields.key);
    }
}
