package com.taskadapter.webui.pages

import java.util

import com.taskadapter.model.GTask
import com.taskadapter.reporting.ErrorReporter
import com.taskadapter.web.uiapi.UISyncConfig
import com.taskadapter.webui.Page.message
import com.taskadapter.webui.export.{ConfirmExportFragment, ExportResultsFragment}
import com.taskadapter.webui.results.ExportResultStorage
import com.taskadapter.webui.{EventTracker, ExportCategory, MonitorWrapper}
import com.vaadin.server.VaadinSession
import com.vaadin.ui._

class ExportHelper(exportResultStorage: ExportResultStorage,
                   onDone: Runnable, showFilePath: Boolean,
                   layout: VerticalLayout,
                   config: UISyncConfig) {

  def onTasksLoaded(tasks: util.List[GTask]): Unit = {
    val dataSourceLabel = config.connector1.getConnectorTypeId
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
    res.addComponent(msg)
    res.addComponent(backButton)
    setContent(layout, res)
  }

  private def showConfirmation(tasks: util.List[GTask]): Unit = {
    val component = ConfirmExportFragment.render(config, tasks, new ConfirmExportFragment.Callback() {
      override def onTasks(selectedTasks: util.List[GTask]): Unit = {
        performExport(selectedTasks)
      }

      override def onCancel(): Unit = {
        onDone.run()
      }
    })
    VaadinSession.getCurrent.lock()
    try {
      setContent(layout, component)
      layout.setExpandRatio(component, 1f)
    } finally VaadinSession.getCurrent.unlock()
  }

  private def performExport(selectedTasks: util.List[GTask]): Unit = {
    if (selectedTasks.isEmpty) {
      Notification.show(message("action.pleaseSelectTasks"))
      return
    }
    val saveProgress = SyncActionComponents.renderSaveIndicator(config.getConnector2)
    saveProgress.setValue(0f)

    setContent(layout, saveProgress)

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
        setContent(layout, exportResult)
      }

      foo()
    }).start()
  }

  private def setContent(content: VerticalLayout, comp: Component): Unit = {
    content.removeAllComponents()
    content.addComponent(comp)
  }
}