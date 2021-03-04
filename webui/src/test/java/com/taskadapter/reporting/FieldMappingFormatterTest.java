package com.taskadapter.reporting;

import com.taskadapter.common.ui.FieldMapping;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.CustomDate;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class FieldMappingFormatterTest {
    @Test
    public void createsANiceString() {
        var result = FieldMappingFormatter.format(
                List.of(new FieldMapping(Optional.of(AllFields.summary()), Optional.of(AllFields.description()), true, "abc"),
                        new FieldMapping(Optional.of(AllFields.doneRatio()), Optional.empty(), false, ""),
                        new FieldMapping(Optional.of(AllFields.dueDate()), Optional.of(CustomDate.apply("some custom text")), false, "")
                ));
        assertThat(result).contains("DueDate                        - CustomDate(some custom text)   selected: false default:");
    }

}
