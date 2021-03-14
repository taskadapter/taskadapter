package com.taskadapter.config;

import com.taskadapter.common.ui.FieldMapping;
import com.taskadapter.common.ui.NewConfigSuggester;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.CustomDate;
import com.taskadapter.model.CustomFloat;
import com.taskadapter.model.CustomString;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class JsonFactoryTest {
    @Test
    public void canSaveAndLoadFields() {
        List<FieldMapping<?>> fieldMappings = List.of(
                NewConfigSuggester.duplicateFieldIntoMapping(AllFields.summary),
                NewConfigSuggester.duplicateFieldIntoMapping(AllFields.doneRatio),
                NewConfigSuggester.duplicateFieldIntoMapping(new CustomDate("f")),
                NewConfigSuggester.duplicateFieldIntoMapping(new CustomFloat("f")),
                NewConfigSuggester.duplicateFieldIntoMapping(new CustomString("f"))
        );
        var str = JsonFactory.toString(fieldMappings);
        var parsed = JsonFactory.fromJsonString(str);
        assertThat(parsed).isEqualTo(fieldMappings);
    }
}
