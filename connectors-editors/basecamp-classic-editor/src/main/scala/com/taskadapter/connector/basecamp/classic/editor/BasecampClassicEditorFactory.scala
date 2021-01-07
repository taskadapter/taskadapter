package com.taskadapter.connector.basecamp.classic.editor

import java.util
import com.taskadapter.connector.basecamp.classic.transport.{BaseCommunicator, ObjectAPIFactory}
import com.taskadapter.connector.basecamp.classic.{BasecampClassicConfig, BasecampClassicConnector, BasecampConfigValidator, BasecampUtils}
import com.taskadapter.connector.definition.exceptions.{BadConfigException, ConnectorException, ProjectNotSetException}
import com.taskadapter.connector.definition.{FieldMapping, WebConnectorSetup}
import com.taskadapter.model.{NamedKeyedObject, NamedKeyedObjectImpl}
import com.taskadapter.vaadin14shim.GridLayout
import com.taskadapter.web.callbacks.DataProvider
import com.taskadapter.web.configeditor.EditorUtil
import com.taskadapter.web.configeditor.EditorUtil.textInput
import com.taskadapter.web.configeditor.server.ServerPanelFactory
import com.taskadapter.web.data.Messages
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.ui.Grids.addTo
import com.taskadapter.web.{ConnectorSetupPanel, DroppingNotSupportedException, PluginEditorFactory}
import com.vaadin.ui.{Alignment, HasComponents, Label, Panel}

import scala.collection.{JavaConverters, Seq}

class BasecampClassicEditorFactory extends PluginEditorFactory[BasecampClassicConfig, WebConnectorSetup] {
  private val BUNDLE_NAME = "com.taskadapter.connector.basecamp.classic.editor.messages"
  private val MESSAGES = new Messages(BUNDLE_NAME)
  private val factory = new ObjectAPIFactory(new BaseCommunicator)
  private val formatter = new BasecampErrorFormatter

  override def isWebConnector = true

  override def getEditSetupPanel(sandbox: Sandbox, setup: WebConnectorSetup): ConnectorSetupPanel =
    ServerPanelFactory.withApiKeyAndLoginPassword(BasecampClassicConnector.ID, BasecampClassicConnector.ID, setup)

  override def createDefaultSetup(sandbox: Sandbox) = new WebConnectorSetup(BasecampClassicConnector.ID, Option.empty,
    "My Basecamp Classic", "https://-my-project-name-here-.basecamphq.com",
    "", "", true, "")

  override def getMiniPanelContents(sandbox: Sandbox, config: BasecampClassicConfig, setup: WebConnectorSetup): HasComponents = {
    val projectPanel = createProjectPanel(config, setup)
    val grid = new GridLayout
    grid.setColumns(2)
    grid.setMargin(true)
    grid.setSpacing(true)
    grid.add(projectPanel)
    grid
  }

  private def createProjectPanel(config: BasecampClassicConfig, setup: WebConnectorSetup) = {
    val projectPanel = new Panel("Project")
    val grid = new GridLayout
    grid.setColumns(4)
    grid.setMargin(true)
    grid.setSpacing(true)
    projectPanel.setContent(grid)
    addProjectRow(config, setup, grid)
    addTodoKeyRow(config, setup, grid)
    projectPanel
  }

  private def addProjectRow(config: BasecampClassicConfig, setup: WebConnectorSetup, grid: GridLayout) = {
    val projectKeyLabel = new Label("Project key:")
    addTo(grid, Alignment.MIDDLE_LEFT, projectKeyLabel)
    val projectKeyField = EditorUtil.textField(config, "projectKey")
    addTo(grid, Alignment.MIDDLE_LEFT, projectKeyField)
    val infoButton = EditorUtil.createButton("Info", "View the project info",
      _ => ShowInfoElement.loadProject(config, setup, formatter, factory)
    )
    addTo(grid, Alignment.MIDDLE_CENTER, infoButton)
    val projectProvider = new DataProvider[util.List[_ <: NamedKeyedObject]]() {
      @throws[ConnectorException]
      override def loadData: util.List[_ <: NamedKeyedObject] = {
        val basecampProjects = BasecampUtils.loadProjects(factory, setup)
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
          projectKeyField.setValue(namedKeyedObject.getKey)
          null
        }

        foo(namedKeyedObject)
      })
    addTo(grid, Alignment.MIDDLE_CENTER, showProjectsButton)
  }

  private def addTodoKeyRow(config: BasecampClassicConfig, setup: WebConnectorSetup, grid: GridLayout) = {
    val todoListKey = new Label("Todo list key:")
    addTo(grid, Alignment.MIDDLE_LEFT, todoListKey)
    val todoKeyField = EditorUtil.textField(config, "todoKey")
    addTo(grid, Alignment.MIDDLE_LEFT, todoKeyField)
    val infoButton = EditorUtil.createButton("Info", "View the todo list info",
      _ => ShowInfoElement.showTodoListInfo(config, setup, formatter, factory)
    )
    addTo(grid, Alignment.MIDDLE_CENTER, infoButton)
    val todoListsProvider = new DataProvider[util.List[_ <: NamedKeyedObject]]() {
      @throws[ConnectorException]
      override def loadData: util.List[_ <: NamedKeyedObject] = {
        val todoLists = BasecampUtils.loadTodoLists(factory, config, setup)
        val objects = new util.ArrayList[NamedKeyedObject]
        import scala.collection.JavaConversions._
        for (todoList <- todoLists) {
          objects.add(new NamedKeyedObjectImpl(todoList.getKey, todoList.getName))
        }
        objects
      }
    }
    val showTodoListsButton = EditorUtil.createLookupButton("...",
      "Show Todo Lists",
      "Select a Todo list",
      "Todo lists on the server",
      todoListsProvider,
      formatter,
      (namedKeyedObject: NamedKeyedObject) => {
        def foo(namedKeyedObject: NamedKeyedObject) = {
          todoKeyField.setValue(namedKeyedObject.getKey)
          null
        }

        foo(namedKeyedObject)
      })
    addTo(grid, Alignment.MIDDLE_CENTER, showTodoListsButton)
  }

  override def validateForSave(config: BasecampClassicConfig, setup: WebConnectorSetup,
                               fieldMappings: Seq[FieldMapping[_]]): Seq[BadConfigException] = {
    val list = new util.ArrayList[BadConfigException]
    if (config.getProjectKey == null || config.getProjectKey.isEmpty) list.add(new ProjectNotSetException)
    JavaConverters.asScalaBuffer(list)
  }

  override def validateForLoad(config: BasecampClassicConfig, setup: WebConnectorSetup): Seq[BadConfigException] =
    BasecampConfigValidator.validateTodoListNoException(config)

  override def describeSourceLocation(config: BasecampClassicConfig, setup: WebConnectorSetup): String = setup.host

  override def describeDestinationLocation(config: BasecampClassicConfig, setup: WebConnectorSetup): String =
    describeSourceLocation(config, setup)

  override def fieldNames: Messages = MESSAGES

  override def formatError(e: Throwable): String = formatter.formatError(e)

  @throws[DroppingNotSupportedException]
  override def validateForDropInLoad(config: BasecampClassicConfig) = throw DroppingNotSupportedException.INSTANCE
}