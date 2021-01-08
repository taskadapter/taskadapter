package com.taskadapter.webui.pages

import com.taskadapter.webui.export.ExportResultsFragment
import com.taskadapter.webui.results.ExportResultFormat
import com.vaadin.flow.component.Component


/**
  * sample code for faster development.
  */
object DevPageFactory {
  def getDevPage(result: ExportResultFormat): Component = {
    val page = new ExportResultsFragment(false)
    page.showExportResult(result)
  }
}
