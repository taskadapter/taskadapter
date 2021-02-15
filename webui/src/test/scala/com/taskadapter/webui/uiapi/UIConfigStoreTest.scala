package com.taskadapter.webui.uiapi

import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.connector.jira.JiraConnector
import com.taskadapter.connector.redmine.RedmineConnector
import com.taskadapter.model.{Description, Field}
import com.taskadapter.web.uiapi.ConfigId
import org.junit.runner.RunWith
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}
import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class UIConfigStoreTest extends FunSpec with ScalaFutures with Matchers with ConfigsTempFolder {

  val redmineData =
    """
      |{"defaultTaskStatus":"New","findUserByName":false,"projectKey":"itest1499045137625","priorities":{"prioritiesMapping":{"High":700,"Urgent":800,"Normal":500,"Low":100,"Immediate":1000}},"saveIssueRelations":false,"defaultTaskType":"Bug"}
    """.stripMargin

  val jiraData =
    """
      |{"component":"","affectedVersion":"","fixForVersion":"","queryId":10300,"projectKey":"TEST","defaultIssueTypeForSubtasks":"Sub-task","priorities":{"prioritiesMapping":{"High":700,"Low":300,"Highest":1000,"Lowest":100,"Medium":500}},"saveIssueRelations":false,"defaultTaskType":"Bug"}
    """.stripMargin


  it("creates Redmine-JIRA config with description mapped") {
    withTempFolder { folder =>
      val store = TestUIConfigStoreFactory.createStore(folder)
      val configId = store.createNewConfig("admin", "label1", RedmineConnector.ID, ConfigFolderTestConfigurer.redmineSetupId,
        JiraConnector.ID, ConfigFolderTestConfigurer.jiraSetupId)
      val config = store.getConfig(configId).get

      val row = findRow(config.getFieldMappings.asScala, Some(Description), Some(Description))
      row.selected shouldBe true
      org.junit.Assert.assertTrue(row.defaultValue == null)
    }
  }

  it("clones config") {
    withTempFolder { folder =>
      val store = TestUIConfigStoreFactory.createStore(folder)
      val config = store.createNewConfig("admin", "label1", RedmineConnector.ID, ConfigFolderTestConfigurer.redmineSetupId,
        JiraConnector.ID, ConfigFolderTestConfigurer.jiraSetupId)
      store.cloneConfig("admin", ConfigId("admin", config.id))

      val configs = store.getUserConfigs("admin")
      configs.size shouldBe 2
    }

  }

  private def findRow(mappings: Seq[FieldMapping[_]], connector1Field: Option[Field[_]], connector2Field: Option[Field[_]]): FieldMapping[_] = {
    val row = mappings.find(m => connector1Field == m.fieldInConnector1 && connector2Field == m.fieldInConnector2)
    row.isDefined shouldBe true
    row.get
  }
}