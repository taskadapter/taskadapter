package com.taskadapter.web

import com.taskadapter.connector.definition.ConnectorSetup
import com.vaadin.ui.Component

trait ConnectorSetupPanel {
  def getUI: Component

  /**
    * @return None if no errors. localized error text otherwise
    */
  def validate: Option[String]

  def showError(String: String) : Unit

  def getResult: ConnectorSetup
}
