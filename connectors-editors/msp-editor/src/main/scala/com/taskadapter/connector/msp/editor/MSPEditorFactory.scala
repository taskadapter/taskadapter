package com.taskadapter.connector.msp.editor

import java.io.File
import java.nio.file.Paths

import com.taskadapter.connector.common.FileNameGenerator
import com.taskadapter.connector.definition.exceptions.BadConfigException
import com.taskadapter.connector.definition.{ConnectorConfig, FileSetup}
import com.taskadapter.connector.msp._
import com.taskadapter.connector.msp.editor.error.{InputFileNameNotSetException, OutputFileNameNotSetException}
import com.taskadapter.web.configeditor.EditorUtil.propertyInput
import com.taskadapter.web.configeditor.file.{FileProcessingResult, LocalModeFilePanel, ServerModeFilePanel}
import com.taskadapter.web.data.Messages
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.{ConnectorSetupPanel, DroppingNotSupportedException, PluginEditorFactory}
import com.vaadin.data.Property
import com.vaadin.data.util.ObjectProperty
import com.vaadin.ui._

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
      case _ =>
    }
    null
  }

  override def getMiniPanelContents(sandbox: Sandbox, config: MSPConfig, setup: FileSetup): ComponentContainer = {
    val layout = new VerticalLayout
    layout.setMargin(true)
    layout.addComponent(createDescriptionElement(config))
    //    layout.addComponent(createFilePanel(sandbox, config))
    layout.addComponent(createInfoReadOnlyPanel)
    layout
  }

  override def getEditSetupPanel(sandbox: Sandbox, maybeSetup: Option[FileSetup]) = new ConnectorSetupPanel() {
    val setup = maybeSetup.getOrElse(FileSetup(MSPConnector.ID, "", "", ""))
    val inputFilePath = new ObjectProperty[String](setup.sourceFile)
    val outputFilePath = new ObjectProperty[String](setup.targetFile)

    override def getUI: Component = {
      if (sandbox.allowLocalFSAccess)
        new LocalModeFilePanel(inputFilePath, outputFilePath)
      else
        createServerModePanel(sandbox, inputFilePath, outputFilePath)
    }

    override def validate(): Option[String] = {
      None
    }

    def getShortLabel(fileName: String): String = {
      Paths.get(fileName).getFileName.toString
    }

    override def getResult: FileSetup = {
      val label = getShortLabel(inputFilePath.getValue)
      FileSetup(MSPConnector.ID, label,
        inputFilePath.getValue, outputFilePath.getValue)
    }

    override def showError(String: String): Unit = {
      // TODO show error
    }
  }

  private def createDescriptionElement(config: ConnectorConfig) = {
    val descriptionLayout = new HorizontalLayout
    descriptionLayout.setSpacing(true)
    descriptionLayout.addComponent(new Label(LABEL_DESCRIPTION_TEXT))
    val labelText = propertyInput(config, "label")
    labelText.setDescription(LABEL_TOOLTIP)
    labelText.setReadOnly(true)
    labelText.addStyleName("label-textfield")
    descriptionLayout.addComponent(labelText)
    descriptionLayout
  }

  private def createInfoReadOnlyPanel = {
    val infoPanel = new MSPInfoPanel
    infoPanel.setHeight("152px")
    infoPanel
  }

  private def createServerModePanel(sandbox: Sandbox, inputFilePath: Property[String], outputFilePath: Property[String]):
  Panel = {
    new ServerModeFilePanel(sandbox.getUserContentDirectory, inputFilePath, outputFilePath,
      (uploadedFile: File) => processFile(sandbox, uploadedFile)
    )
  }

  @throws[BadConfigException]
  override def validateForSave(config: MSPConfig, setup: FileSetup): Unit = {
    if (setup.targetFile.isEmpty) throw new OutputFileNameNotSetException
  }

  @throws[BadConfigException]
  override def validateForLoad(config: MSPConfig, setup: FileSetup): Unit = {
    if (setup.sourceFile.isEmpty) throw new InputFileNameNotSetException
  }

  @throws[BadConfigException]
  @throws[DroppingNotSupportedException]
  override def validateForDropInLoad(config: MSPConfig): Unit = {
    // Always valid!
  }

  override def describeSourceLocation(config: MSPConfig, setup: FileSetup): String =
    new File(setup.sourceFile).getName

  override def describeDestinationLocation(config: MSPConfig, setup: FileSetup): String =
    new File(setup.targetFile).getName

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

  @throws[BadConfigException]
  override def updateForSave(config: MSPConfig, sandbox: Sandbox, setup: FileSetup): Boolean = {
    if (!setup.targetFile.isEmpty) return false
    val newPath = FileNameGenerator.createSafeAvailableFile(sandbox.getUserContentDirectory, "MSP_export_%d.xml").getAbsolutePath
    if (newPath == null) throw new OutputFileNameNotSetException

    // TODO TA3 this won't work!
//    config.setOutputAbsoluteFilePath(newPath)
//    config.setInputAbsoluteFilePath(newPath)
    true
  }

  override def isWebConnector: Boolean = false
}
