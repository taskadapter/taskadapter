package com.taskadapter.web.uiapi;

import com.taskadapter.common.ui.FieldMapping;
import com.taskadapter.model.AllFields;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class UISyncConfigTest {
    @Test
    public void reversesMappings() {
        assertThat(
                UISyncConfig.reverse(List.of(
                        new FieldMapping(AllFields.summary, AllFields.description, true, ""),
                        new FieldMapping(Optional.empty(), Optional.of(AllFields.assigneeFullName), true, "")
                        )
                ))
                .containsOnly(
                        new FieldMapping(AllFields.description, AllFields.summary, true, ""),
                        new FieldMapping(Optional.of(AllFields.assigneeFullName), Optional.empty(), true, ""));
    }

}
