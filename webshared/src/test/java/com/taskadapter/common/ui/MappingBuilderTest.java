package com.taskadapter.common.ui;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.definition.ExportDirection;
import com.taskadapter.model.CustomString;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MappingBuilderTest {
    @Test
    public void skipsFieldsThatAreNotSelected() {
        var rows = MappingBuilder.build(
                List.of(FieldMapping.apply(new CustomString("summary"), new CustomString("summary"), false, "default")),
                ExportDirection.RIGHT

        );
        assertThat(rows).isEmpty();
    }

    @Test
    public void exportRightProcessesSelectedFields() {
        List<FieldRow<?>> rows = MappingBuilder.build(
                List.of(FieldMapping.apply(
                        new CustomString("JiraSummary"), new CustomString("RedmineSummary"), true, "default")),
                ExportDirection.RIGHT

        );
        assertThat(rows.get(0).getSourceField().get().getFieldName()).isEqualTo("JiraSummary");
        assertThat(rows.get(0).getTargetField().get().getFieldName()).isEqualTo("RedmineSummary");
    }

    @Test
    public void exportLeftProcessesSelectedFields() {
        List<FieldRow<?>> rows = MappingBuilder.build(
                List.of(FieldMapping.apply(
                        new CustomString("JiraSummary"), new CustomString("RedmineSummary"), true, "default")),
                ExportDirection.LEFT

        );
        assertThat(rows.get(0).getSourceField().get().getFieldName()).isEqualTo("RedmineSummary");
        assertThat(rows.get(0).getTargetField().get().getFieldName()).isEqualTo("JiraSummary");
    }
}
