package com.taskadapter.web.uiapi;

import com.taskadapter.PluginManager;
import com.taskadapter.connector.jira.JiraConnector;
import com.taskadapter.connector.redmine.RedmineConnector;
import com.taskadapter.connector.testlib.TestDataLoader;
import com.taskadapter.webui.service.EditorManager;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UIConfigServiceTest {
    private static final String redmineData = TestDataLoader.loadAsString("uiapi/redmine-data.json");
    private static final String jiraData = TestDataLoader.loadAsString("uiapi/jira-data.json");

    private static final UIConfigService uiConfigService = new UIConfigService(new PluginManager(),
            EditorManager.fromResource("editors.txt"));

    @Test
    public void redmineConfigCanBeParsed() {
        var config = uiConfigService.createRichConfig(RedmineConnector.ID, redmineData);
        assertThat(config).isNotNull();
    }

    @Test
    public void jiraConfigCanBeParsed() {
        var config = uiConfigService.createRichConfig(JiraConnector.ID, jiraData);
        assertThat(config).isNotNull();
    }
}