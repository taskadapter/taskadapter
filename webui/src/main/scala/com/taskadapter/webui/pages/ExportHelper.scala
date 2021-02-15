package com.taskadapter.webui.pages

import java.util

import com.taskadapter.model.GTask
import com.taskadapter.reporting.ErrorReporter
import com.taskadapter.web.uiapi.UISyncConfig
import com.taskadapter.webui.Page.message
import com.taskadapter.webui.export.{ConfirmExportFragment, ExportResultsFragment}
import com.taskadapter.webui.results.ExportResultStorage
import com.taskadapter.webui.{ConfigOperations, EventTracker, ExportCategory, MonitorWrapper}
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.VerticalLayout

class ExportHelper(exportResultStorage: ExportResultStorage,
                   onDone: Runnable, showFilePath: Boolean,
                   layout: VerticalLayout,
                   config: UISyncConfig,
                   configOps: ConfigOperations) {

  def onTasksLoaded(tasks: util.List[GTask]): Unit = {
    val dataSourceLabel = config.getConnector1.getConnectorTypeId
    EventTracker.trackEvent(ExportCategory, "loaded_tasks", dataSourceLabel, tasks.size())

    if (tasks.isEmpty) showNoDataLoaded()
    else showConfirmation(tasks)
  }

  /**
    * Shows "no data loaded" content.
    */
  def showNoDataLoaded(): Unit = {
    val res = new VerticalLayout
    val msg = new Label(message("export.noDataWasLoaded"))
    msg.setWidth("800px")
    val backButton = new Button(message("button.back"))
    backButton.addClickListener(_ => onDone.run())
    res.add(msg)
    res.add(backButton)
    setContent(res)
  }

  private def showConfirmation(tasks: util.List[GTask]): Unit = {
    val confirmationComponent = ConfirmExportFragment.render(configOps, config, tasks, new ConfirmExportFragment.Callback() {
      override def onTasks(selectedTasks: util.List[GTask]): Unit = {
        performExport(selectedTasks)
      }

      override def onCancel(): Unit = {
        onDone.run()
      }
    })

    setContent(confirmationComponent)
  }

  private def performExport(selectedTasks: util.List[GTask]): Unit = {
    if (selectedTasks.isEmpty) {
      Notification.show(message("action.pleaseSelectTasks"))
      return
    }
    val saveProgress = SyncActionComponents.renderSaveIndicator(config.getConnector2)
    saveProgress.setValue(0f)

    setContent(saveProgress)

    val wrapper = new MonitorWrapper(saveProgress)
    new Thread(() => {
      def foo() = {
        val saveResult = config.saveTasks(selectedTasks, wrapper)
        ExportResultsLogger.log(saveResult)
        exportResultStorage.store(saveResult)
        if (saveResult.hasErrors) ErrorReporter.reportIfAllowed(config, saveResult)
        val targetLabel = config.getConnector2.getConnectorTypeId
        val sourceAndTarget = config.getConnector1.getConnectorTypeId + " - " + targetLabel
        val exportResult = new ExportResultsFragment(showFilePath).showExportResult(saveResult)
        EventTracker.trackEvent(ExportCategory, "finished_export", sourceAndTarget)
        EventTracker.trackEvent(ExportCategory, "finished_saving_tasks", targetLabel)
        EventTracker.trackEvent(ExportCategory, "created_tasks", targetLabel, saveResult.createdTasksNumber)
        EventTracker.trackEvent(ExportCategory, "updated_tasks", targetLabel, saveResult.updatedTasksNumber)
        EventTracker.trackEvent(ExportCategory, "tasks_with_errors", targetLabel, saveResult.taskErrors.size)
        setContent(exportResult)
      }

      foo()
    }).start()
  }

  private def setContent(comp: Component): Unit = {
    layout.getUI.get().access(() => {
      layout.removeAll()
      layout.add(comp)
    })
  }
}