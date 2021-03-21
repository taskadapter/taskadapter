package com.taskadapter.connector.msp.editor

import com.taskadapter.common.ui.FieldMapping
import com.taskadapter.connector.common.FileNameGenerator
import com.taskadapter.connector.definition.FileSetup
import com.taskadapter.connector.definition.exceptions.BadConfigException
import com.taskadapter.connector.msp._
import com.taskadapter.connector.msp.editor.error.{InputFileNameNotSetException, OutputFileNameNotSetException}
import com.taskadapter.web.configeditor.file.{FileProcessingResult, LocalModeFilePanel, ServerModeFilePanel}
import com.taskadapter.web.data.Messages
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.uiapi.{DefaultSavableComponent, SavableComponent}
import com.taskadapter.web.{ConnectorSetupPanel, PluginEditorFactory}
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.orderedlayout.VerticalLayout

import java.io.File
import java.nio.file.Paths
import java.util.{Collections, Optional}
import scala.collection.JavaConverters._
import scala.collection.mutable

class MSPEditorFactory extends PluginEditorFactory[MSPConfig, FileSetup] {
  private val BUNDLE_NAME = "com.taskadapter.connector.msp.messages"
  private val LABEL_DESCRIPTION_TEXT = "Description:"
  private val LABEL_TOOLTIP = "Text to show for this connector on 'Export' button. Enter any text."
  private val UPLOAD_SUCCESS = "Upload success"
  private val UPLOAD_MPP_SUCCESS = "File uploaded and successfully converted to XML"
  private val SAVE_FILE_FAILED = "Save file error" // error of saving after upload

  private val messages = new Messages(BUNDLE_NAME)

  override def formatError(e: Throwable): String = {
    e match {
      case relationType: UnsupportedRelationType => return messages.format("errors.unsupportedRelation",
        messages.get("relations." + relationType.getRelationType))
      case _: InputFileNameNotSetException => return messages.get("error.inputFileNameNotSet")
      case _: OutputFileNameNotSetException => return messages.get("error.outputFileNameNotSet")
      case _ => e.getMessage
    }
  }

  // TODO 14 use the binder
  override def getMiniPanelContents(sandbox: Sandbox, config: MSPConfig, setup: FileSetup): SavableComponent = {
    val layout = new VerticalLayout
    layout.setMargin(true)
    layout.add(createInfoReadOnlyPanel)
    // TODO 14 update the save function
    new DefaultSavableComponent(layout, () => {
      true
    })
  }

  override def getEditSetupPanel(sandbox: Sandbox, setup: FileSetup) = new ConnectorSetupPanel() {
    var savableComponent : SavableComponent = if (sandbox.allowLocalFSAccess)
      new LocalModeFilePanel(setup)
    else
      createServerModePanel(sandbox, setup)

    override def getComponent: Component = {
      savableComponent.getComponent
    }

    override def validate(): Optional[String] = {
      Optional.empty()
    }

    override def getResult: FileSetup = {
      savableComponent.save()
      setup
    }

    override def showError(String: String): Unit = {
      // TODO show error
    }
  }

  def getShortLabel(fileName: String): String = {
    Paths.get(fileName).getFileName.toString
  }

  private def createInfoReadOnlyPanel = {
    val infoPanel = new MSPInfoPanel
    infoPanel.setHeight("152px")
    infoPanel
  }

  private def createServerModePanel(sandbox: Sandbox, fileSetup: FileSetup): SavableComponent = {
    new ServerModeFilePanel(sandbox.getUserContentDirectory, fileSetup,
      (uploadedFile: File) => processFile(sandbox, uploadedFile)
    )
  }

  override def validateForSave(config: MSPConfig, setup: FileSetup, fieldMappings: java.util.List[FieldMapping[_]]):
  java.util.List[BadConfigException] = {
    // empty target file name is valid because it will be generated in [[updateForSave]]
    // right before the export
    Collections.emptyList();
  }

  override def validateForLoad(config: MSPConfig, setup: FileSetup): java.util.List[BadConfigException] = {
    val seq = new mutable.ListBuffer[BadConfigException]()
    if (setup.getSourceFile.isEmpty) seq += new InputFileNameNotSetException
    seq.asJava
  }

  override def validateForDropInLoad(config: MSPConfig): Unit = {
    // Always valid!
  }

  override def describeSourceLocation(config: MSPConfig, setup: FileSetup): String =
    new File(setup.getSourceFile).getName

  override def describeDestinationLocation(config: MSPConfig, setup: FileSetup): String =
    new File(setup.getTargetFile).getName

  private def processFile(sandbox: Sandbox, uploadedFile: File): FileProcessingResult = {
    val fileName = uploadedFile.getName
    // check if MPP file
    val isMpp = fileName.toLowerCase.endsWith(MSPFileReader.MPP_SUFFIX_LOWERCASE)
    if (!isMpp) return new FileProcessingResult(uploadedFile, UPLOAD_SUCCESS)
    val f = new File(sandbox.getUserContentDirectory, fileName)
    val newFilePath = MSPUtils.convertMppProjectFileToXml(f.getAbsolutePath)
    if (newFilePath == null) return new FileProcessingResult(null, SAVE_FILE_FAILED)
    new FileProcessingResult(new File(newFilePath), UPLOAD_MPP_SUCCESS)
  }

  override def isWebConnector: Boolean = false

  override def createDefaultSetup(sandbox: Sandbox): FileSetup = {
    val newPath = FileNameGenerator.findSafeAvailableFileName(sandbox.getUserContentDirectory, "MSP_%d.xml").getAbsolutePath
    if (newPath == null) throw new OutputFileNameNotSetException
    val label = getShortLabel(newPath)
    FileSetup.apply(MSPConnector.ID, label, newPath, newPath)
  }

  override def fieldNames: Messages = messages
}
