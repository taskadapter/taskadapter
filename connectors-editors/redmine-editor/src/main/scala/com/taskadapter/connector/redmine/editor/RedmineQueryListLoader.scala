package com.taskadapter.connector.redmine.editor

import java.util

import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.connector.definition.exceptions.BadConfigException
import com.taskadapter.connector.redmine.RedmineConfig
import com.taskadapter.model.NamedKeyedObject
import com.taskadapter.redmineapi.NotFoundException
import com.taskadapter.web.callbacks.DataProvider
import com.vaadin.flow.component.notification.Notification

class RedmineQueryListLoader(config: RedmineConfig, setup: WebConnectorSetup) extends DataProvider[java.util.List[_ <: NamedKeyedObject]] {
  override def loadData(): util.List[_ <: NamedKeyedObject] = {
    try {
      RedmineLoaders.loadData(setup, config.getProjectKey)
    } catch {
      case e: NotFoundException =>
        Notification.show("The server did not return any saved queries.\n" + "NOTE: This operation is only supported by Redmine 1.3.0+")
        null
      case e: BadConfigException =>
        throw e
      case e: Exception =>
        throw new RuntimeException(e)
    }
  }
}
