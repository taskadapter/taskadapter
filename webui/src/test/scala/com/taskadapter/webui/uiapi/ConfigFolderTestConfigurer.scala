package com.taskadapter.webui.uiapi

import java.io.File
import java.nio.file.{Files, Paths}

import com.taskadapter.web.uiapi.SetupId

object ConfigFolderTestConfigurer {

  val jiraSetupId = SetupId("Atlassian_JIRA_1.json")
  val jiraCloudSetupId = SetupId("Atlassian_Jira_cloud.json")
  val redmineSetupId = SetupId("Redmine_1.json")

  /**
    * Configures test config folder with JIRA/Redmine credentials.
    *
    * @return folder with user configs
    */
  def configure(rootFolder: File): Unit = {
    List(jiraSetupId.id,
      jiraCloudSetupId.id,
      redmineSetupId.id,
      SetupId("Microsoft_Project_1.json").id,
      SetupId("GitHub1.json").id,
      SetupId("Mantis1.json").id
    )
      .foreach { resourceName =>
        val adminFolder = new File(rootFolder, "admin")
        adminFolder.mkdirs()
        Files.copy(Paths.get(getClass.getClassLoader.getResource(resourceName).getPath),
          new File(adminFolder, resourceName).toPath)
      }
  }
}