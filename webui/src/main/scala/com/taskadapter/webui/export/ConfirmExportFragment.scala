package com.taskadapter.webui.export

import java.util

import com.taskadapter.config.StorageException
import com.taskadapter.model.GTask
import com.taskadapter.web.uiapi.UISyncConfig
import com.taskadapter.webui.{ConfigOperations, Page}
import com.taskadapter.webui.action.MyTree
import com.taskadapter.webui.config.TaskFieldsMappingFragment
import com.vaadin.ui._
import org.slf4j.LoggerFactory

object ConfirmExportFragment {
  private val LOGGER = LoggerFactory.getLogger(ConfirmExportFragment.getClass)

  /**
    * Confirm export fragment callbacks.
    */
  trait Callback {
    /**
      * Notifies about selected tasks.
      *
      * @param selectedTasks selected tasks.
      */
    def onTasks(selectedTasks: util.List[GTask]): Unit

    /**
      * Notifies about process cancellation.
      */
    def onCancel(): Unit
  }

  /**
    * Renders export confirmation fragment.
    *
    * @return confirmation dialog.
    */
  def render(configOps: ConfigOperations, config: UISyncConfig, initalTasks: util.List[GTask], callback: Callback): Component = {
    val layout = new VerticalLayout
    layout.setSpacing(true)
    val destination = config.getConnector2.getDestinationLocation + " (" + config.getConnector2.getConnectorTypeId + ")"
    val text1 = new Label(Page.message("exportConfirmation.pleaseConfirm", destination))
    layout.addComponent(text1)
    val connectorTree = new MyTree
    connectorTree.setSizeFull()
    connectorTree.setTasks(initalTasks)
    layout.addComponent(connectorTree)
    val buttonsLayout = new HorizontalLayout
    val goButton = new Button(Page.message("button.go"))
    buttonsLayout.addComponent(goButton)
    val backButton = new Button(Page.message("button.cancel"))
    backButton.addClickListener(_ => callback.onCancel())
    buttonsLayout.addComponent(backButton)
    layout.addComponent(buttonsLayout)
    val taskFieldsMappingFragment = new TaskFieldsMappingFragment(Page.MESSAGES,
      config.getConnector1, config.getConnector2, config.getNewMappings)
    layout.addComponent(taskFieldsMappingFragment.getUI)
    goButton.addClickListener(_ => {
      try
        configOps.saveConfig(config)
      catch {
        case e: StorageException =>
          LOGGER.error(Page.message("action.cantSaveUpdatedConfig", e.getMessage), e)
      }
      callback.onTasks(connectorTree.getSelectedRootLevelTasks)
    })
    layout
  }
}