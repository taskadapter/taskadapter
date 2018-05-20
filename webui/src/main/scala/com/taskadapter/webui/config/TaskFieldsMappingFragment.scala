package com.taskadapter.webui.config

import java.util.UUID

import com.google.common.base.Strings
import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.connector.definition.exceptions.BadConfigException
import com.taskadapter.model.{Assignee, Field, GUser, Reporter}
import com.taskadapter.web.configeditor.Validatable
import com.taskadapter.web.data.Messages
import com.taskadapter.webui.Page
import com.vaadin.data.util.{BeanItemContainer, MethodProperty}
import com.vaadin.server.Sizeable.Unit.PIXELS
import com.vaadin.server.ThemeResource
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui.{AbstractComponent, Alignment, Button, CheckBox, ComboBox, Component, Embedded, GridLayout, Label, Panel, TextField, VerticalLayout}

import scala.collection.JavaConverters._
import scala.collection.Seq
import scala.collection.mutable.ListBuffer

object TaskFieldsMappingFragment {
  val COLUMN_DESCRIPTION = 0
  val COLUMN_HELP = 1
  val COLUMN_LEFT_CONNECTOR = 2
  val COLUMN_RIGHT_CONNECTOR = 3
  val COLUMN_DEFAULT_VALUE = 4
  val COLUMN_REMOVE = 5
  val COLUMNS_NUMBER = 6
  // TODO maybe merge this help file with all the other localized strings? but it has some rules about namings...
  val BUNDLE_NAME = "help"
  val HELP_MESSAGES = new Messages(BUNDLE_NAME)
  val HELP_ICON_RESOURCE = new ThemeResource("../runo/icons/16/help.png")
}

class EditablePojoMappings(mappings: Seq[FieldMapping[_]],
                           connector1FieldLoader: ConnectorFieldLoader, connector2FieldLoader: ConnectorFieldLoader) {
  var editablePojoMappings: ListBuffer[EditableFieldMapping] = ListBuffer()

  editablePojoMappings = mappings.map(ro =>
    new EditableFieldMapping(UUID.randomUUID.toString,
      ro.fieldInConnector1.map(_.name).getOrElse(""),
      ro.fieldInConnector2.map(_.name).getOrElse(""),
      ro.selected,
      if (ro.defaultValue == null) "" else ro.defaultValue.asInstanceOf[String]))
    .to[ListBuffer]

  def removeFieldFromList(field: EditableFieldMapping) = {
    editablePojoMappings = editablePojoMappings.filter(
      e => !(e.uniqueIdForTemporaryMap == field.uniqueIdForTemporaryMap))
  }

  def validate(): Unit = {
    MappingsValidator.validate(editablePojoMappings)
  }

  def getElements: Iterable[FieldMapping[_]] = editablePojoMappings.map(e =>
    new FieldMapping(
      getField(e.fieldInConnector1),
      getField(e.fieldInConnector2),
      e.selected,
      DefaultValueResolver.getTag(getField(e.fieldInConnector1).get).parseDefault(e.defaultValue)
    )
  )

  def getField[T](fieldName: String): Option[Field[T]] = {
    if (Strings.isNullOrEmpty(fieldName)) {
      None
    } else {
      val field = connector1FieldLoader.getTypeForFieldName(fieldName)
      Some(field.asInstanceOf[Field[T]])
    }
  }

  def add(m: EditableFieldMapping): Unit = {
    editablePojoMappings += m
  }

  def removeEmptyRows() = {
    editablePojoMappings = editablePojoMappings.filter(e =>
      !(Strings.isNullOrEmpty(e.fieldInConnector1) && Strings.isNullOrEmpty(e.fieldInConnector2)))
  }

}

class TaskFieldsMappingFragment(messages: Messages,
                                connector1SupportedFields: Seq[Field[_]],
                                connector1Label: String,
                                connector2SupportedFields: Seq[Field[_]],
                                connector2Label: String,
                                mappings: Seq[FieldMapping[_]]) extends Validatable {
  val ui = new Panel(messages.get("editConfig.mappings.caption"))
  val layout = new VerticalLayout
  val gridLayout = new GridLayout
  val editablePojoMappings = new EditablePojoMappings(mappings,
    new ConnectorFieldLoader(connector1SupportedFields),
    new ConnectorFieldLoader(connector2SupportedFields))

  layout.addComponent(gridLayout)
  rebuildMappingUI()
  ui.setContent(layout)
  addNewRowButton()

  def rebuildMappingUI(): Unit = {
    gridLayout.removeAllComponents()
    configureGridLayout()
    addTableHeaders()
    addFieldsToUI()
  }

  private def configureGridLayout(): Unit = {
    gridLayout.setMargin(true)
    gridLayout.setSpacing(true)
    gridLayout.setRows(mappings.size + 3)
    gridLayout.setColumns(TaskFieldsMappingFragment.COLUMNS_NUMBER)
  }

  private def addTableHeaders(): Unit = {
    val label2 = new Label(messages.get("editConfig.mappings.exportFieldHeader"))
    label2.addStyleName("fieldsTitle")
    label2.setWidth(40, PIXELS)
    gridLayout.addComponent(label2, TaskFieldsMappingFragment.COLUMN_DESCRIPTION, 0)
    gridLayout.setComponentAlignment(label2, Alignment.MIDDLE_LEFT)
    val label = new Label(" ")
    label.setWidth(20, PIXELS)
    gridLayout.addComponent(label, TaskFieldsMappingFragment.COLUMN_HELP, 0)

    val label1 = new Label(connector1Label)
    label1.addStyleName("fieldsTitle")
    label1.setWidth(230, PIXELS)
    gridLayout.addComponent(label1, TaskFieldsMappingFragment.COLUMN_LEFT_CONNECTOR, 0)
    gridLayout.setComponentAlignment(label1, Alignment.MIDDLE_LEFT)

    val label3 = new Label(connector2Label)
    label3.addStyleName("fieldsTitle")
    label3.setWidth(230, PIXELS)
    gridLayout.addComponent(label3, TaskFieldsMappingFragment.COLUMN_RIGHT_CONNECTOR, 0)
    gridLayout.setComponentAlignment(label3, Alignment.MIDDLE_LEFT)

    val label4 = new Label(messages.get("editConfig.mappings.defaultValueColumn"))
    label4.addStyleName("fieldsTitle")
    label4.setWidth(180, PIXELS)
    gridLayout.addComponent(label4, TaskFieldsMappingFragment.COLUMN_DEFAULT_VALUE, 0)
    gridLayout.setComponentAlignment(label4, Alignment.MIDDLE_LEFT)
    val column5label = new Label
    column5label.setWidth(30, PIXELS)
    gridLayout.addComponent(column5label, TaskFieldsMappingFragment.COLUMN_REMOVE, 0)
    gridLayout.setComponentAlignment(column5label, Alignment.MIDDLE_LEFT)
    gridLayout.addComponent(new Label("<hr>", ContentMode.HTML), 0, 1, TaskFieldsMappingFragment.COLUMNS_NUMBER - 1, 1)
  }

  /**
    * Add all rows to mappings table
    */
  private def addFieldsToUI() = {
    editablePojoMappings.editablePojoMappings.foreach(e => addRowToVaadinForm(e))
  }

  /**
    * Add a row to mapping table:
    * selected, tooltip, connector 1 field name, connector 2 field name, default value.
    */
  private def addRowToVaadinForm(field: EditableFieldMapping) = {
    addCheckbox(field)
    // TODO TA3 help is per connector field, not for the whole row now.
    val helpForField = null //getHelpForField(field);
    if (helpForField != null) addHelp(helpForField)
    else addEmptyCell()
    addConnectorField(connector1SupportedFields, field, "fieldInConnector1")
    addConnectorField(connector2SupportedFields, field, "fieldInConnector2")
    addTextFieldForDefaultValue(field)
    addRemoveRowButton(field)
  }

  private def addRemoveRowButton(field: EditableFieldMapping) = {
    val button = new Button(Page.message("editConfig.mappings.buttonRemove"))
    button.addClickListener(_ => removeRow(field))
    gridLayout.addComponent(button)
    gridLayout.setComponentAlignment(button, Alignment.MIDDLE_RIGHT)
  }

  private def removeRow(field: EditableFieldMapping) = {
    var row = 0
    while (row < gridLayout.getRows) {
      val component = gridLayout.getComponent(0, row)
      if (component != null) { // deleted some rows, so this is no longer valid
        val data = component.asInstanceOf[AbstractComponent].getData
        if (field.uniqueIdForTemporaryMap == data) {
          gridLayout.removeRow(row)
          editablePojoMappings.removeFieldFromList(field) // TODO this is a no-op when called from removeEmptyRows.
        }

      }
      row += 1
    }
  }

  private def addCheckbox(field: EditableFieldMapping) = {
    val checkbox = new CheckBox
    checkbox.setData(field.uniqueIdForTemporaryMap)
    val selected = new MethodProperty[Boolean](field, "selected")
    checkbox.setPropertyDataSource(selected)
    gridLayout.addComponent(checkbox)
    gridLayout.setComponentAlignment(checkbox, Alignment.MIDDLE_CENTER)
  }

  private def addTextFieldForDefaultValue(mapping: EditableFieldMapping) = {
    val field = new TextField
    val methodProperty = new MethodProperty[String](mapping, "defaultValue")
    field.setPropertyDataSource(methodProperty)
    gridLayout.addComponent(field)
    gridLayout.setComponentAlignment(field, Alignment.MIDDLE_CENTER)
  }

  private def addHelp(helpForField: String) = {
    val helpIcon = new Embedded(null, TaskFieldsMappingFragment.HELP_ICON_RESOURCE)
    helpIcon.setDescription(helpForField)
    gridLayout.addComponent(helpIcon)
    gridLayout.setComponentAlignment(helpIcon, Alignment.MIDDLE_CENTER)
  }

  private def addEmptyCell() = {
    val emptyLabel = new Label(" ")
    gridLayout.addComponent(emptyLabel)
    gridLayout.setComponentAlignment(emptyLabel, Alignment.MIDDLE_LEFT)
  }

  case class FieldBeanItem(vaadinId: String, className: String, friendlyName: String)

  private def addConnectorField(connectorFields: Seq[Field[_]], fieldMapping: EditableFieldMapping, classFieldName: String) = {
    val container = new BeanItemContainer[String](classOf[String])
    val mappedTo = new MethodProperty[String](fieldMapping, classFieldName)
    val fieldNames = connectorFields.map((field: Field[_]) => field.name)
      .toList
    container.addAll(fieldNames.asJava)
    val combo = new ComboBox(null, container)
    combo.setPropertyDataSource(mappedTo)
    combo.setNewItemsAllowed(true)
    combo.setWidth("90%")
    gridLayout.addComponent(combo)
    gridLayout.setComponentAlignment(combo, Alignment.MIDDLE_LEFT)
    val currentFieldName = if (classFieldName == "fieldInConnector1") fieldMapping.fieldInConnector1
    else fieldMapping.fieldInConnector2
    combo.select(currentFieldName)
  }

  private def addNewRowButton() = {
    val button = new Button(Page.message("editConfig.mappings.buttonAdd"))
    button.addClickListener(_ => {
      val m = new EditableFieldMapping(UUID.randomUUID.toString, "", "", false, "")
      editablePojoMappings.add(m)
      addRowToVaadinForm(m)
    })
    layout.addComponent(button)
  }

  @throws[BadConfigException]
  override def validate(): Unit = {
    editablePojoMappings.validate()
  }

  def getUI: Component = ui

  def removeEmptyRows(): Unit = {
    editablePojoMappings.removeEmptyRows()
    rebuildMappingUI()
  }

  def getElements: Iterable[FieldMapping[_]] = editablePojoMappings.getElements

}