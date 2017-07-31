package com.taskadapter.web

import com.taskadapter.connector.definition.ConnectorSetup
import com.taskadapter.connector.definition.exceptions.BadConfigException
import com.vaadin.ui.Component

trait ConnectorSetupPanel {
  def getUI: Component

  @throws[BadConfigException]
  def validate: Unit

  def getResult: ConnectorSetup
}
