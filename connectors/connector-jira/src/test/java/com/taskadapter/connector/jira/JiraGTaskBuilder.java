package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.testlib.RandomStringGenerator;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.GTask;

import java.util.Date;

public class JiraGTaskBuilder {
    public static GTask taskWithType(String value) {
        var task = new GTask();
        task.setValue(AllFields.summary, "task " + new Date().getTime());
        task.setValue(AllFields.taskType, value);
        return task;
    }

    public static JiraGTaskBuilder builderWithSummary() {
        return new JiraGTaskBuilder(RandomStringGenerator.randomAlphaNumeric(30));
    }

    private GTask task = new GTask();

    public JiraGTaskBuilder(String summary) {
        // summary is pretty much always required
        task.setValue(AllFields.summary, summary);
    }

    public JiraGTaskBuilder withDescription() {
        task.setValue(AllFields.description, "description " + new Date().getTime());
        return this;
    }

    public JiraGTaskBuilder withPriority(Integer value) {
        task.setValue(AllFields.priority, value);
        return this;
    }

    public JiraGTaskBuilder withParentId(TaskId value) {
        task.setParentIdentity(value);
        return this;
    }

    public JiraGTaskBuilder withId(Long value) {
        task.setId(value);
        return this;
    }

    public JiraGTaskBuilder withType(String value) {
        task.setValue(AllFields.taskType, value);
        return this;
    }

    public GTask build() {
        return task;
    }
}
