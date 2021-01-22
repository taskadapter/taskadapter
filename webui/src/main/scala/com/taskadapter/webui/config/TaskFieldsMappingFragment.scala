package com.taskadapter.webui.config

import java.util.UUID

import com.google.common.base.Strings
import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.connector.definition.exceptions.BadConfigException
import com.taskadapter.model.Field
import com.taskadapter.web.configeditor.{EditorUtil, Validatable}
import com.taskadapter.web.data.Messages
import com.taskadapter.web.uiapi.SavableComponent
import com.taskadapter.webui.Page
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.binder.Binder

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
//  val HELP_ICON_RESOURCE = new ThemeResource("../runo/icons/16/help.png")
}

class EditablePojoMappings(mappings: Seq[FieldMapping[_]],
                           connector1FieldLoader: ConnectorFieldLoader, connector2FieldLoader: ConnectorFieldLoader) {
  var editablePojoMappings: ListBuffer[EditableFieldMapping] = mappings.map(ro =>
    new EditableFieldMapping(
      new Binder[EditableFieldMapping](classOf[EditableFieldMapping]),
      UUID.randomUUID.toString,
      ro.fieldInConnector1.map(_.name).getOrElse(""),
      ro.fieldInConnector2.map(_.name).getOrElse(""),
      ro.selected,
      if (ro.defaultValue == null) "" else ro.defaultValue))
    .to[ListBuffer]

  def removeFieldFromList(field: EditableFieldMapping) = {
    editablePojoMappings = editablePojoMappings.filter(
      e => !(e.getUniqueIdForTemporaryMap == field.getUniqueIdForTemporaryMap()))
  }

  def validate(): Unit = {
    MappingsValidator.validate(editablePojoMappings)
  }

  def getElements: Iterable[FieldMapping[_]] = editablePojoMappings
    // skip empty rows
    .filter(e =>
      !(Strings.isNullOrEmpty(e.getFieldInConnector1) && Strings.isNullOrEmpty(e.getFieldInConnector2)))
    // convert to the output format
    .map(e =>
      new FieldMapping(
        getField(e.getFieldInConnector1, connector1FieldLoader),
        getField(e.getFieldInConnector2, connector2FieldLoader),
        e.getSelected,
        e.getDefaultValue
      )
    )

  def getField[T](fieldName: String, fieldLoader: ConnectorFieldLoader): Option[Field[T]] = {
    if (Strings.isNullOrEmpty(fieldName)) {
      None
    } else {
      val field = fieldLoader.getTypeForFieldName(fieldName)
      Some(field.asInstanceOf[Field[T]])
    }
  }

  def add(m: EditableFieldMapping): Unit = {
    editablePojoMappings += m
  }
}

class TaskFieldsMappingFragment(messages: Messages,
                                connector1SupportedFields: Seq[Field[_]],
                                connector1Messages: Messages,
                                connector1Label: String,
                                connector2SupportedFields: Seq[Field[_]],
                                connector2Messages: Messages,
                                connector2Label: String,
                                mappings: Seq[FieldMapping[_]]) extends SavableComponent with Validatable {
  val layout = new VerticalLayout
  val gridLayout = new FormLayout()
  gridLayout.setResponsiveSteps(
    new FormLayout.ResponsiveStep("2em", 1),
    new FormLayout.ResponsiveStep("2em", 2),
    new FormLayout.ResponsiveStep("10em", 3),
    new FormLayout.ResponsiveStep("10em", 4),
    new FormLayout.ResponsiveStep("10em", 5),
    new FormLayout.ResponsiveStep("5em", 6));

  val editablePojoMappings = new EditablePojoMappings(mappings,
    new ConnectorFieldLoader(connector1SupportedFields),
    new ConnectorFieldLoader(connector2SupportedFields))

  layout.add(
    new Label(messages.get("editConfig.mappings.caption")),
    gridLayout)

  rebuildMappingUI()
  addNewRowButton()

  def rebuildMappingUI(): Unit = {
    gridLayout.removeAll()
    addTableHeaders()
    addFieldsToUI()
  }

  private def addTableHeaders(): Unit = {
    val label2 = new Label(messages.get("editConfig.mappings.exportFieldHeader"))
    label2.addClassName("fieldsTitle")
    label2.setWidth("40px")
    gridLayout.add(label2)
    val label = new Label(" ")
    label.setWidth("20px");
    gridLayout.add(label)

    val label1 = new Label(connector1Label)
    label1.addClassName("fieldsTitle")
    label1.setWidth("230px")
    gridLayout.add(label1)

    val label3 = new Label(connector2Label)
    label3.addClassName("fieldsTitle")
    label3.setWidth("230px");
    gridLayout.add(label3)

    val label4 = new Label(messages.get("editConfig.mappings.defaultValueColumn"))
    label4.addClassName("fieldsTitle")
    label4.setWidth("180px");
    gridLayout.add(label4)
    val column5label = new Label
    column5label.setWidth("30px");
    gridLayout.add(column5label)
//    gridLayout.add(new Label("<hr>", ContentMode.HTML), 0, 1, TaskFieldsMappingFragment.COLUMNS_NUMBER - 1, 1)
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

    addCheckbox(field.getBinder)
/////////////////     TODO TA3 help is per connector field, not for the whole row now.
    val helpForField = null //getHelpForField(field);
    if (helpForField != null) addHelp(helpForField)
    else addEmptyCell()
    addConnectorField(field.getBinder, connector1SupportedFields, connector1Messages, field.getFieldInConnector1, "fieldInConnector1")
    addConnectorField(field.getBinder, connector2SupportedFields, connector2Messages, field.getFieldInConnector2, "fieldInConnector2")
    addTextFieldForDefaultValue(field.getBinder)
    addRemoveRowButton(field)
    field.getBinder.readBean(field)
  }

  private def addRemoveRowButton(field: EditableFieldMapping) = {
    val button = new Button(Page.message("editConfig.mappings.buttonRemove"),
      _ => removeRow(field))
    gridLayout.add(button)
  }

  private def removeRow(field: EditableFieldMapping) = {
    // TODO 14 need to save?
    // save the current fields info into the data model first
    save()
    editablePojoMappings.removeFieldFromList(field)
    rebuildMappingUI()
  }

  private def addCheckbox(binder: Binder[EditableFieldMapping]) = {
    val checkbox = EditorUtil.checkbox("", "Include this field when exporting data", binder, "selected")
//    checkbox.setData(field.uniqueIdForTemporaryMap)
    gridLayout.add(checkbox)
  }

  private def addTextFieldForDefaultValue(binder: Binder[EditableFieldMapping]) = {
    val field = EditorUtil.textInput(binder, "defaultValue")
    gridLayout.add(field)
  }

  private def addHelp(helpForField: String) = {
//    val helpIcon = new Embedded(null, TaskFieldsMappingFragment.HELP_ICON_RESOURCE)
//    helpIcon.setDescription(helpForField)
//    gridLayout.add(helpIcon)
//    gridLayout.setComponentAlignment(helpIcon, Alignment.MIDDLE_CENTER)
  }

  private def addEmptyCell() = {
    val emptyLabel = new Label(" ")
    gridLayout.add(emptyLabel)
  }

  private def addConnectorField(binder: Binder[EditableFieldMapping],
                                connectorFields: Seq[Field[_]], connectorMessages: Messages,
                                selectedValue: String,
                                propertyName: String) :Unit = {
    val combobox = new ComboBox[String]()
    combobox.setItemLabelGenerator(fieldName =>
      Option(connectorMessages.getNoDefault(fieldName)).getOrElse(fieldName))
    combobox.setItems(connectorFields.map(_.name).asJava)
    combobox.setAllowCustomValue(true)
    gridLayout.add(combobox)
    combobox.setValue(selectedValue)
    binder.bind(combobox, propertyName)
  }

  private def addNewRowButton() = {
    val button = new Button(Page.message("editConfig.mappings.buttonAdd"))
    button.addClickListener(_ => {
      val m = new EditableFieldMapping(
        new Binder[EditableFieldMapping](classOf[EditableFieldMapping]),
        UUID.randomUUID.toString, "", "", false, "")
      editablePojoMappings.add(m)
      addRowToVaadinForm(m)
    })
    layout.add(button)
  }

  @throws[BadConfigException]
  override def validate(): Unit = {
    editablePojoMappings.validate()
  }

  def getElements: Iterable[FieldMapping[_]] = editablePojoMappings.getElements

  override def getComponent: Component = layout

  override def save(): Boolean = {
    editablePojoMappings.editablePojoMappings.foreach(fieldMapping => {
      fieldMapping.getBinder.writeBean(fieldMapping)
    })
    true
  }
}