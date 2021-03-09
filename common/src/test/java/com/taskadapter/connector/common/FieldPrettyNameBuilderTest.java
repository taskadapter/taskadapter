package com.taskadapter.connector.common;

import com.taskadapter.model.AllFields;
import com.taskadapter.model.CustomDate;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FieldPrettyNameBuilderTest {
    @Test
    public void customField() {
        assertThat(FieldPrettyNameBuilder.getPrettyFieldName(new CustomDate("Actual Finish")))
                .isEqualTo("CustomDate(Actual Finish)");
    }

    @Test
    public void regularField() {
        assertThat(FieldPrettyNameBuilder.getPrettyFieldName(AllFields.summary))
                .isEqualTo("Summary");
    }

    @Test
    public void priority() {
        assertThat(FieldPrettyNameBuilder.getPrettyFieldName(AllFields.priority))
                .isEqualTo("Priority");
    }
}
