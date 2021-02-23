package com.taskadapter.webui.export

import java.util
import com.taskadapter.config.StorageException
import com.taskadapter.model.GTask
import com.taskadapter.web.ui.HtmlLabel
import com.taskadapter.web.uiapi.UISyncConfig
import com.taskadapter.webui.{ConfigOperations, Page}
import com.taskadapter.webui.action.MyTree
import com.taskadapter.webui.config.TaskFieldsMappingFragment
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.{HorizontalLayout, VerticalLayout}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

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
  def render(configOps: ConfigOperations,
             config: UISyncConfig,
             initialTasks: util.List[GTask], callback: Callback): Component = {
    val resolver = config.getPreviouslyCreatedTasksResolver()
    val layout = new VerticalLayout
    layout.setSpacing(true)

    val loadedTasksLabel = new HtmlLabel(Page.message("exportConfirmation.loadedTasks",
      initialTasks.size() + "",
      config.getConnector1.getLabel,
      config.getConnector1.getSourceLocation))
    val destinationLocation = config.getConnector2.getDestinationLocation
    val destinationWithDecoration = destinationLocation + " (" + config.getConnector2.getConnectorTypeId + ")"
    val text1 = new Label(Page.message("exportConfirmation.pleaseConfirm", destinationWithDecoration))

    layout.add(loadedTasksLabel,
      text1)

    val connectorTree = new MyTree(resolver, initialTasks, destinationLocation)
    layout.add(connectorTree.getTree)
    val buttonsLayout = new HorizontalLayout
    val goButton = new Button(Page.message("button.go"))
    buttonsLayout.add(goButton)
    val backButton = new Button(Page.message("button.cancel"))
    backButton.addClickListener(_ => callback.onCancel())
    buttonsLayout.add(backButton)
    layout.add(buttonsLayout)
    val taskFieldsMappingFragment = new TaskFieldsMappingFragment(Page.MESSAGES,
      config.getConnector1.getAllFields, config.getConnector1.fieldNames, config.getConnector1.getLabel,
      config.getConnector2.getAllFields, config.getConnector2.fieldNames, config.getConnector2.getLabel,
      config.getNewMappings.asScala)

    def getPossiblyUpdatedConfig = {
      val newFieldMappings = taskFieldsMappingFragment.getElements.toSeq.asJava
      new UISyncConfig(config.getTaskKeeperLocationStorage,
        config.getConfigId,
        config.getLabel,
        config.getConnector1,
        config.getConnector2,
        newFieldMappings,
        config.isReversed)
    }

    layout.add(taskFieldsMappingFragment.getComponent)
    goButton.addClickListener(_ => {
      try {
        configOps.saveConfig(getPossiblyUpdatedConfig)
      } catch {
        case e: StorageException =>
          LOGGER.error(Page.message("action.cantSaveUpdatedConfig", e.getMessage), e)
      }
      callback.onTasks(connectorTree.getSelectedRootLevelTasks)
    })
    layout
  }
}
