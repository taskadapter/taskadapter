package com.taskadapter.webui.pages

import java.util

import com.taskadapter.model.GTask
import com.taskadapter.web.uiapi.UISyncConfig
import com.taskadapter.webui.Page.message
import com.taskadapter.webui.export.{ConfirmExportFragment, ExportResultsFragment}
import com.taskadapter.webui.results.ExportResultStorage
import com.taskadapter.webui.{ConfigOperations, MonitorWrapper, Tracker}
import com.vaadin.server.VaadinSession
import com.vaadin.ui._

class ExportHelper(configOps: ConfigOperations,
                   exportResultStorage: ExportResultStorage,
                   tracker: Tracker, onDone: Runnable, showFilePath: Boolean,
                   layout: VerticalLayout,
                   config: UISyncConfig) {

  def onTasksLoaded(tasks: util.List[GTask]): Unit = {
    val labelForTracking = config.connector1.getConnectorTypeId + " - " + config.getConnector2.getConnectorTypeId
    tracker.trackEvent("export", "loaded_tasks", labelForTracking)

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
    val component = ConfirmExportFragment.render(configOps, config, tasks, new ConfirmExportFragment.Callback() {
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
        val labelForTracking = config.getConnector1.getConnectorTypeId + " - " + config.getConnector2.getConnectorTypeId
        val exportResult = new ExportResultsFragment(onDone, showFilePath).showExportResult(saveResult)
        tracker.trackEvent("export", "finished_saving_tasks", labelForTracking)
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