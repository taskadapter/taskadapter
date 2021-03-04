package com.taskadapter.webui.config;

import com.taskadapter.common.ui.FieldMapping;
import com.taskadapter.model.AllFields;
import com.taskadapter.webui.Page;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskFieldsMappingFragmentTest {
    @Test
    public void emptyDefaultValueForNonStringBasedFieldIsConvertedToEmptyString() {
        var f = new TaskFieldsMappingFragment(Page.MESSAGES,
                Arrays.asList(AllFields.createdOn()), Page.MESSAGES, "JIRA",
                Arrays.asList(AllFields.createdOn(), AllFields.updatedOn()), Page.MESSAGES, "Redmine",
                List.of(new FieldMapping(AllFields.createdOn(), AllFields.createdOn(), true, null))
        );

        assertThat(f.getElements()).hasSize(1);
        assertThat(f.getElements().get(0).getDefaultValue()).isEqualTo("");
    }
}
