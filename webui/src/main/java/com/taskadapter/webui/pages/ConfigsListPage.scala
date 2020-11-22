package com.taskadapter.webui.pages

import java.util.Comparator

import com.taskadapter.web.event.{EventBusImpl, NewConfigPageRequested, ShowConfigPageRequested}
import com.taskadapter.web.uiapi.{ConfigId, UISyncConfig}
import com.taskadapter.webui.service.Preservices
import com.taskadapter.webui.{BasePage, ConfigOperations, Page, SessionController}
import com.vaadin.ui._
import com.vaadin.ui.themes.BaseTheme
import org.slf4j.LoggerFactory

object ConfigsListPage {

  /**
    * Comparator for configuration files.
    */
  class ConfigComparator(
                          /**
                            * Name of the current user.
                            */
                          val userName: String) extends Comparator[UISyncConfig] {
    override def compare(o1: UISyncConfig, o2: UISyncConfig): Int = {
      val isMyConfig1 = userName == o1.getOwnerName
      val isMyConfig2 = userName == o2.getOwnerName
      if (isMyConfig1 != isMyConfig2) return if (isMyConfig1) -1
      else 1
      val ucomp = o1.getOwnerName.compareTo(o2.getOwnerName)
      if (ucomp != 0) return ucomp
      o1.getLabel.compareTo(o2.getLabel)
    }
  }

}

class ConfigsListPage() extends BasePage {

  private val log = LoggerFactory.getLogger(classOf[ConfigsListPage])

  private val configOps: ConfigOperations = SessionController.buildConfigOperations()
  private val services: Preservices = SessionController.getServices

  val showAll = services.settingsManager.adminCanManageAllConfigs &&
    SessionController.getUserContext.authorizedOps.canManagerPeerConfigs

  val displayMode = if (showAll) DisplayMode.ALL_CONFIGS
  else DisplayMode.OWNED_CONFIGS

  private var configs = Seq[UISyncConfig]()

  val layout = new VerticalLayout
  layout.setSpacing(true)
  val actionPanel = new HorizontalLayout
  actionPanel.setWidth("100%")
  actionPanel.setSpacing(true)
  val addButton = new Button(Page.message("configsPage.buttonNewConfig"))
  addButton.addClickListener(_ => EventBusImpl.post(NewConfigPageRequested()))
  actionPanel.addComponent(addButton)
  actionPanel.setComponentAlignment(addButton, Alignment.MIDDLE_LEFT)

  val filterPanel = new HorizontalLayout
  val filterField = new TextField
  filterField.addTextChangeListener(e => filterFields(e.getText))
  filterPanel.addComponent(new Label(Page.message("configsPage.filter")))
  filterPanel.addComponent(filterField)
  filterPanel.setSpacing(true)
  actionPanel.addComponent(filterPanel)
  actionPanel.setComponentAlignment(filterPanel, Alignment.MIDDLE_RIGHT)
  val configsTopLevelLayout = new HorizontalLayout()
  configsTopLevelLayout.setSpacing(true)
  configsTopLevelLayout.setSizeFull()

  val configsLayout = new VerticalLayout()
  configsLayout.setWidth("100%")

  configsTopLevelLayout.addComponent(configsLayout)
  configsTopLevelLayout.setComponentAlignment(configsLayout, Alignment.TOP_CENTER)

  layout.addComponent(actionPanel)
  layout.addComponent(configsTopLevelLayout)
  layout.setComponentAlignment(actionPanel, Alignment.TOP_LEFT)

  def refreshConfigs() = {
    val loadedConfigs = if (showAll) configOps.getManageableConfigs
    else configOps.getOwnedConfigs
    configs = loadedConfigs.sortBy(c => c.getOwnerName)
    setDisplayedConfigs(configs)
    filterFields(filterField.getValue)
  }

  private def setDisplayedConfigs(dispConfigs: Seq[UISyncConfig]): Unit = {
    configsLayout.removeAllComponents()
    dispConfigs.foreach(config => {
      configsLayout.addComponent(createConfigComponent(config))
    })
  }

  private def createConfigComponent(config: UISyncConfig): Component = {
    val layout = new HorizontalLayout()
    val configLabel = config.label
    val link = new Button(configLabel)
    link.addStyleName(BaseTheme.BUTTON_LINK)
    link.addClickListener(_ => showConfigSummary(config.configId))
    layout.addComponent(link)
    layout
  }

  private def showConfigSummary(configId: ConfigId): Unit = {
    EventBusImpl.post(ShowConfigPageRequested(configId))
  }

  private def filterFields(filterStr: String): Unit = {
    val words = if (filterStr == null) new Array[String](0)
    else filterStr.toLowerCase.split(" +")
    val res = configs.filter(config => matches(config, words))
    setDisplayedConfigs(res)
  }

  private def matches(config: UISyncConfig, filters: Array[String]): Boolean = {
    if (filters.length == 0) return true
    for (name <- filters) {
      val confName = displayMode.nameOf(config)
      if (!confName.toLowerCase.contains(name) && !config.getConnector1.getLabel.toLowerCase.contains(name) && !config.getConnector2.getLabel.toLowerCase.contains(name)) return false
    }
    true
  }

  def ui = layout
}