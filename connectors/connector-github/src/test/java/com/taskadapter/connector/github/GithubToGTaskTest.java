package com.taskadapter.connector.github;

import com.taskadapter.connector.testlib.TestDataLoader;
import com.taskadapter.model.AllFields;
import org.eclipse.egit.github.core.Issue;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GithubToGTaskTest {
    @Test
    public void convertsBasicTask() {
        var issue = (Issue) TestDataLoader.load("issue.json", Issue.class);
        var task = GithubToGTask.toGtask(issue);
        assertThat(task.getValue(AllFields.summary)).isEqualTo("task 1");
    }
}
