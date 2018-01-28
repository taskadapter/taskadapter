package com.taskadapter.connector.jira

import java.util

import com.google.common.base.Strings
import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.connector.definition.exceptions.{ConnectorException, ServerURLNotSetException}
import com.taskadapter.model.NamedKeyedObject
import com.taskadapter.web.callbacks.DataProvider

class JiraQueryListLoader(jiraConfig: JiraConfig, setup: WebConnectorSetup) extends DataProvider[java.util.List[_ <: NamedKeyedObject]] {

  /**
    * Loads data.
    *
    * @return loaded data.
    * @throws ConnectorException if current state is invalid.
    */
  override def loadData(): util.List[_ <: NamedKeyedObject] = {
    if (Strings.isNullOrEmpty(setup.host)) throw new ServerURLNotSetException
    new JiraConnector(jiraConfig, setup).getFilters
  }
}
