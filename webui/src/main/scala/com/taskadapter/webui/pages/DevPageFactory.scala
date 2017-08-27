package com.taskadapter.webui.pages

import com.taskadapter.webui.export.ExportResultsFragment
import com.taskadapter.webui.results.ExportResultFormat
import com.vaadin.ui.Component

/**
  * sample code for faster development.
  */
object DevPageFactory {
  def getDevPage(result: ExportResultFormat, onDone: Runnable): Component = {
    val page = new ExportResultsFragment(onDone, false)
    page.showExportResult(result)
  }
}
