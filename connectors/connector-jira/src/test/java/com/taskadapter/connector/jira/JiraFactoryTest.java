package com.taskadapter.connector.jira;

import com.taskadapter.model.AllFields;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JiraFactoryTest {
    @Test
    public void haveAssignee() {
        assertThat(new JiraFactory().getDefaultFieldsForNewConfig()).contains(AllFields.assigneeLoginName);
    }

    @Test
    public void skipOptionalOnes() {
        assertThat(new JiraFactory().getDefaultFieldsForNewConfig()).doesNotContain(AllFields.reporterLoginName,
                AllFields.estimatedTime,
                AllFields.dueDate,
                AllFields.id, AllFields.key);
    }
}
