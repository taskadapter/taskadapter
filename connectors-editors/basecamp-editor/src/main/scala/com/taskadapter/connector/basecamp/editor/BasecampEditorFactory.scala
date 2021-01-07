package com.taskadapter.connector.basecamp.editor

import java.util
import com.taskadapter.connector.basecamp.transport.{BaseCommunicator, ObjectAPI, ObjectAPIFactory}
import com.taskadapter.connector.basecamp.{BasecampConfig, BasecampConnector, BasecampUtils, BasecampValidator}
import com.taskadapter.connector.definition.exceptions.{BadConfigException, ConnectorException, ProjectNotSetException}
import com.taskadapter.connector.definition.{FieldMapping, WebConnectorSetup}
import com.taskadapter.model.{NamedKeyedObject, NamedKeyedObjectImpl}
import com.taskadapter.vaadin14shim.GridLayout
import com.taskadapter.web.callbacks.DataProvider
import com.taskadapter.web.configeditor.EditorUtil.{propertyInput, textInput}
import com.taskadapter.web.configeditor.server.ServerPanelFactory
import com.taskadapter.web.configeditor.{EditorUtil, Editors}
import com.taskadapter.web.data.Messages
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.ui.Grids.addTo
import com.taskadapter.web.{ConnectorSetupPanel, DroppingNotSupportedException, PluginEditorFactory}
import com.vaadin.data.util.MethodProperty
import com.vaadin.ui.{Alignment, CheckBox, HasComponents, Label, Panel}

import scala.collection.JavaConverters._
import scala.collection.Seq

class BasecampEditorFactory extends PluginEditorFactory[BasecampConfig, WebConnectorSetup] {
  private val BUNDLE_NAME = "com.taskadapter.connector.basecamp.editor.messages"
  private val MESSAGES = new Messages(BUNDLE_NAME)
  private val factory = new ObjectAPIFactory(new BaseCommunicator)
  private val formatter = new BasecampErrorFormatter

  override def isWebConnector = true

  override def getMiniPanelContents(sandbox: Sandbox, config: BasecampConfig, setup: WebConnectorSetup): HasComponents = {
    val projectPanel = createProjectPanel(config, setup)
    val grid = new GridLayout
    grid.setColumns(2)
    grid.setMargin(true)
    grid.setSpacing(true)
    grid.add(projectPanel)
    grid
  }

  override def getEditSetupPanel(sandbox: Sandbox, setup: WebConnectorSetup): ConnectorSetupPanel = ServerPanelFactory.withApiKeyAndLoginPassword(BasecampConnector.ID, BasecampConnector.ID, setup)

  override def createDefaultSetup(sandbox: Sandbox) = new WebConnectorSetup(BasecampConnector.ID, Option.empty, "My Basecamp 2", ObjectAPI.BASECAMP_URL, "", "", false, "")

  private def createProjectPanel(config: BasecampConfig, setup: WebConnectorSetup) = {
    val projectPanel = new Panel("Project")
    val grid = new GridLayout
    grid.setColumns(4)
    grid.setMargin(true)
    grid.setSpacing(true)
    projectPanel.setContent(grid)
    addAccountIdRow(config, grid)
    addProjectRow(config, setup, grid)
    addTodoKeyRow(config, setup, grid)
    addCompletedCheckboxRow(config, grid)
    addFindUsersCheckboxRow(config, grid)
    projectPanel
  }

  private def addFindUsersCheckboxRow(config: BasecampConfig, grid: GridLayout) = {
    grid.add(Editors.createFindUsersElement(new MethodProperty[java.lang.Boolean](config, "findUserByName")))
    grid.add(new Label(""))
    grid.add(new Label(""))
    grid.add(new Label(""))
  }

  private def addCompletedCheckboxRow(config: BasecampConfig, grid: GridLayout) = {
    val loadCompletedTasksCheckbox = new CheckBox("Load completed items")
    loadCompletedTasksCheckbox.setPropertyDataSource(new MethodProperty[String](config, "loadCompletedTodos"))
    grid.add(loadCompletedTasksCheckbox)
    grid.add(new Label(""))
    grid.add(new Label(""))
    grid.add(new Label(""))
  }

  private def addAccountIdRow(config: BasecampConfig, grid: GridLayout) = {
    val accountIdLabel = new Label("Account Id:")
    grid.add(accountIdLabel)
    val accountIdField = propertyInput(config, "accountId")
    grid.add(accountIdField)
    grid.add(new Label(""))
    grid.add(new Label(""))
  }

  private def addProjectRow(config: BasecampConfig, setup: WebConnectorSetup, grid: GridLayout) = {
    val projectKeyLabel = new Label("Project key:")
    addTo(grid, Alignment.MIDDLE_LEFT, projectKeyLabel)
    val projectKeyProperty = new MethodProperty[String](config, "projectKey")
    addTo(grid, Alignment.MIDDLE_LEFT, textInput(projectKeyProperty))
    val infoButton = EditorUtil.createButton("Info", "View the project info",
      _ => ShowInfoElement.loadProject(config, setup, formatter, factory)
    )

    addTo(grid, Alignment.MIDDLE_CENTER, infoButton)
    val projectProvider = new DataProvider[util.List[_ <: NamedKeyedObject]]() {
      @throws[ConnectorException]
      override def loadData: util.List[_ <: NamedKeyedObject] = {
        val basecampProjects = BasecampUtils.loadProjects(factory, config, setup)
        val objects = new util.ArrayList[NamedKeyedObject]
        import scala.collection.JavaConversions._
        for (project <- basecampProjects) {
          objects.add(new NamedKeyedObjectImpl(project.getKey, project.getName))
        }
        objects
      }
    }
    val showProjectsButton = EditorUtil.createLookupButton("...",
      "Show list of available projects on the server.",
      "Select project",
      "List of projects on the server",
      projectProvider,
      formatter,
      (namedKeyedObject: NamedKeyedObject) => {
        def foo(namedKeyedObject: NamedKeyedObject) = {
          projectKeyProperty.setValue(namedKeyedObject.getKey)
          null
        }

        foo(namedKeyedObject)
      })
    addTo(grid, Alignment.MIDDLE_CENTER, showProjectsButton)
  }

  private def addTodoKeyRow(config: BasecampConfig, setup: WebConnectorSetup, grid: GridLayout) = {
    val todoListKey = new Label("Todo list key:")
    addTo(grid, Alignment.MIDDLE_LEFT, todoListKey)
    val todoKeyProperty = new MethodProperty[String](config, "todoKey")
    addTo(grid, Alignment.MIDDLE_LEFT, textInput(todoKeyProperty))
    val infoButton = EditorUtil.createButton("Info", "View the todo list info",
      _ => ShowInfoElement.showTodoListInfo(config, setup, formatter, factory)
    )
    addTo(grid, Alignment.MIDDLE_CENTER, infoButton)
    val todoListsProvider: DataProvider[java.util.List[_ <: NamedKeyedObject]] = () => {
      val todoLists = BasecampUtils.loadTodoLists(factory, config, setup)
      todoLists.asScala.map(todoList => NamedKeyedObjectImpl(todoList.getKey, todoList.getName)).asJava.asInstanceOf[java.util.List[NamedKeyedObject]]
    }
    val showTodoListsButton = EditorUtil.createLookupButton("...", "Show Todo Lists",
      "Select a Todo list",
      "Todo lists on the server",
      todoListsProvider,
      formatter,
      (namedKeyedObject: NamedKeyedObject) => {
        def foo(namedKeyedObject: NamedKeyedObject) = {
          todoKeyProperty.setValue(namedKeyedObject.getKey)
          null
        }

        foo(namedKeyedObject)
      })
    addTo(grid, Alignment.MIDDLE_CENTER, showTodoListsButton)
  }

  override def validateForSave(config: BasecampConfig, setup: WebConnectorSetup, fieldMappings: Seq[FieldMapping[_]]): Seq[BadConfigException] = {
    if (config.getProjectKey == null || config.getProjectKey.isEmpty) {
      return Seq(new ProjectNotSetException)
    }
    Seq()
  }

  override def validateForLoad(config: BasecampConfig, setup: WebConnectorSetup): Seq[BadConfigException] = BasecampValidator.validateConfig(config)

  override def describeSourceLocation(config: BasecampConfig, setup: WebConnectorSetup): String = setup.host

  override def describeDestinationLocation(config: BasecampConfig, setup: WebConnectorSetup): String = describeSourceLocation(config, setup)

  override def fieldNames: Messages = MESSAGES

  override def formatError(e: Throwable): String = formatter.formatError(e)

  @throws[DroppingNotSupportedException]
  override def validateForDropInLoad(config: BasecampConfig) = throw DroppingNotSupportedException.INSTANCE
}