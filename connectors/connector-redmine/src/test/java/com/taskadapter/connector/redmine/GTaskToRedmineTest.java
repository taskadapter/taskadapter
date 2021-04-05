package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.exception.FieldConversionException;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.GTask;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GTaskToRedmineTest {
    private static Project project;

    @BeforeClass
    public static void beforeAll() {
        project = new Project(null);
    }

    @Test
    public void fieldsAreSet() throws FieldConversionException {
        var task = new GTask()
                .setValue(AllFields.summary, "summary")
                .setValue(AllFields.description, "descr");
        Issue issue = convert(task);
        assertThat(issue.getSubject()).isEqualTo("summary");
        assertThat(issue.getDescription()).isEqualTo("descr");
    }

    private Issue convert(GTask gTask) throws FieldConversionException {
        return getConverter().convert(gTask);
    }

    private GTaskToRedmine getConverter() {
        var config = new RedmineConfig();
        return new GTaskToRedmine(config,
                null, project, new RedmineUserCache(List.of()), Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList());
    }


}
