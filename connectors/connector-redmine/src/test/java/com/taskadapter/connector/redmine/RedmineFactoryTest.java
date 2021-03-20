package com.taskadapter.connector.redmine;

import com.taskadapter.model.AllFields;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RedmineFactoryTest {
    @Test
    public void defaultFieldsHaveAssignee() {
        assertThat(new RedmineFactory().getDefaultFieldsForNewConfig()).contains(AllFields.assigneeLoginName);
    }

    @Test
    public void defaultFieldsDoNotHaveOptionalFields() {
        assertThat(new RedmineFactory().getDefaultFieldsForNewConfig())
                .doesNotContain(AllFields.updatedOn, AllFields.id, AllFields.key);
    }
}
