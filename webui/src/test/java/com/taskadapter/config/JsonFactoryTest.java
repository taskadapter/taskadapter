package com.taskadapter.config;

import com.taskadapter.connector.NewConfigSuggester;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.CustomDate;
import com.taskadapter.model.CustomFloat;
import com.taskadapter.model.CustomString;
import org.junit.Test;
import scala.collection.JavaConverters;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class JsonFactoryTest {
    @Test
    public void canSaveAndLoadFields() {
        var fieldMappings = List.of(
                NewConfigSuggester.duplicateFieldIntoMapping(AllFields.summary()),
                NewConfigSuggester.duplicateFieldIntoMapping(AllFields.doneRatio()),
                NewConfigSuggester.duplicateFieldIntoMapping(CustomDate.apply("f")),
                NewConfigSuggester.duplicateFieldIntoMapping(CustomFloat.apply("f")),
                NewConfigSuggester.duplicateFieldIntoMapping(CustomString.apply("f"))
        );
        var str = JsonFactory.toString(JavaConverters.asScalaBuffer(fieldMappings));
        var parsed = JavaConverters.seqAsJavaList(JsonFactory.fromJsonString(str));
        assertThat(parsed).isEqualTo(fieldMappings);
    }
}
