package com.taskadapter.web.uiapi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ConfigFolderTestConfigurer {
    public static SetupId jiraSetupId = new SetupId("Atlassian_JIRA_1.json");
    public static SetupId jiraCloudSetupId = new SetupId("Atlassian_Jira_cloud.json");
    public static SetupId redmineSetupId = new SetupId("Redmine_1.json");

    /**
     * Configures test config folder with JIRA/Redmine credentials.
     *
     * @return folder with user configs
     */
    public static void configure(File rootFolder) {
        List.of(jiraSetupId.getId(),
                jiraCloudSetupId.getId(),
                redmineSetupId.getId(),
                new SetupId("Microsoft_Project_1.json").getId(),
                new SetupId("GitHub1.json").getId(),
                new SetupId("Mantis1.json").getId()
        ).forEach(resourceName -> {
            var adminFolder = new File(rootFolder, "admin");
            adminFolder.mkdirs();
            try {
                Files.copy(Paths.get(ConfigFolderTestConfigurer.class.getClassLoader().getResource(resourceName).getPath()),
                        new File(adminFolder, resourceName).toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
