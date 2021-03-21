package com.taskadapter.web.configeditor.file;

import com.taskadapter.connector.definition.FileSetup;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.ui.HtmlLabel;
import com.taskadapter.web.uiapi.SavableComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ServerModeFilePanel extends VerticalLayout implements SavableComponent {
    private static final Logger logger = LoggerFactory.getLogger(ServerModeFilePanel.class);

    // TODO show this limit on the webpage
    static final int MAX_FILE_SIZE_BYTES = 5000000;

    private static final String TITLE = "Microsoft Project file";
    private static final String COMBOBOX_INPUT_PROMPT = "Select an existing file";
    static final String FILE_WILL_GENERATED_HINT = "File will be auto-created on export";

    static final String COMBOBOX_ITEM_PROPERTY = "name";
    static final String DATE_FORMAT = "d MMM yyyy h:mm:ss a z";
    static final String DOWNLOAD_BUTTON_CAPTION = "Download file";
    private static final String UPLOAD_BUTTON_CAPTION = "Upload new";
    static final String UPLOAD_FAILED = "Upload failed";
    static final String UPLOADING = "Uploading";
    private static final String DELETE_BUTTON_CAPTION = "Delete";
    private static final String QUESTION_DELETE_FILE = "Delete selected file?";
    static final String FILE_DELETED_SUCCESS = "File deleted";
    static final String FILE_DELETED_FAILED = "File deletion error";

    private static final String COMBOBOX_WIDTH = "175px";
    private FileSetup fileSetup;

    private Label statusLabel;
    private ServerModelFilePanelPresenter presenter;
    private Upload uploadButton;
    private Button downloadButton;
    private ComboBox comboBox;
//    private ProgressIndicator progressIndicator;
    private Button deleteButton;
    private final Binder<FileSetup> binder = new Binder<>(FileSetup.class);

    public ServerModeFilePanel(File filesDirectory, FileSetup fileSetup, UploadProcessor uploadProcessor) {
        this.fileSetup = fileSetup;
        this.presenter = new ServerModelFilePanelPresenter(filesDirectory,
                uploadProcessor);
        buildUI();
//        presenter.setView(this);
//        presenter.init(findInitialFile(fileSetup));
    }

    private void buildUI() {
        var caption = EditorUtil.createCaption(TITLE);
        VerticalLayout view = new VerticalLayout();
        view.add(new HtmlLabel("Msp panel is currently not supported for server mode. " +
                "please switch the app into local mode in 'Configure' page first."));
//        view.add(createComboboxPanel());
//        view.add(createDateLabelPanel());
//        view.add(createUploadPanel());
        add(caption, view);
    }
/*
    private HorizontalLayout createComboboxPanel() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.add(createComboBox());
        layout.add(createDownloadButton());
        layout.add(createDeleteButton());
        return layout;
    }

    private HorizontalLayout createDateLabelPanel() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);

        layout.add(new HtmlLabel("&nbsp;&nbsp;&nbsp;"));

        statusLabel = new HtmlLabel("");
        layout.add(statusLabel);

        progressIndicator = new ProgressIndicator(new Float(0.0));
        progressIndicator.setPollingInterval(500);
        progressIndicator.setVisible(false);
        layout.add(progressIndicator);

        return layout;
    }

    private VerticalLayout createUploadPanel() {
        VerticalLayout layout = new VerticalLayout();

        layout.add(new HtmlLabel("<hr>"));

        HorizontalLayout bottomToolLayout = new HorizontalLayout();
        bottomToolLayout.add(createUploadButton());

        layout.add(bottomToolLayout);

        return layout;
    }

    private Component createUploadButton() {
        // TODO VAADIN7 bug: the error indication stays on the page forever when an upload fails. (found by Maxim)
        uploadButton = new Upload();

        uploadButton.setReceiver(presenter.getUploadReceiver());
        uploadButton.setImmediate(true);
        uploadButton.setButtonCaption(UPLOAD_BUTTON_CAPTION);

        uploadButton.addListener(Upload.StartedEvent.class, presenter, "uploadStarted");
        uploadButton.addListener(Upload.SucceededEvent.class, presenter, "uploadSucceeded");
        uploadButton.addListener(Upload.FailedEvent.class, presenter, "uploadFailed");
        uploadButton.addListener(new Upload.ProgressListener() {
            public void updateProgress(long readBytes, long contentLength) {
                progressIndicator.setValue(readBytes / (float) contentLength);
            }
        });

        return uploadButton;
    }

    private Component createDeleteButton() {
        deleteButton = new Button(DELETE_BUTTON_CAPTION);
        deleteButton.addClickListener(event -> showConfirmationDialog(QUESTION_DELETE_FILE));
        return deleteButton;
    }

    private void showConfirmationDialog(String question) {
        PopupDialog dialog = PopupDialog.confirm(question, () -> {
            presenter.deleteSelectedFile();
//            return BoxedUnit.UNIT;
        });
        getUI().addWindow(dialog);
    }


   private Component createComboBox() {
        comboBox = new ComboBox();
        comboBox.setItemCaptionPropertyId(COMBOBOX_ITEM_PROPERTY);// Sets the combobox to show a certain property as the item caption
        comboBox.setWidth(COMBOBOX_WIDTH);
        comboBox.setNewItemsAllowed(false);
        comboBox.setInputPrompt(COMBOBOX_INPUT_PROMPT);

        comboBox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        comboBox.setFilteringMode(FilteringMode.STARTSWITH);
        comboBox.setImmediate(true);
        comboBox.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                Property property = event.getProperty();
                Property selected = presenter.getFileList().getContainerProperty(property.getValue(), COMBOBOX_ITEM_PROPERTY);
                if (selected != null) {
                    presenter.onFileSelected((String) selected.getValue());
                } else {
                    presenter.onNoFileSelected();
                }
                fileSetup.setSourceFile(presenter.getSelectedFileNameOrEmpty());
                fileSetup.setTargetFile(presenter.getSelectedFileNameOrEmpty());
            }
        });
        return comboBox;
    }

    private Component createDownloadButton() {
        downloadButton = new Button(DOWNLOAD_BUTTON_CAPTION);
        return downloadButton;
    }

    public void setUploadEnabled(boolean flag) {
        // upload disabled in case of uploading process
        progressIndicator.setVisible(!flag);
        if (flag) {
            progressIndicator.setValue(0f);
        }

        uploadButton.setEnabled(flag);
    }

    public Button getDownloadButton() {
        return downloadButton;
    }

    public void setDownloadEnabled(boolean flag) {
        downloadButton.setEnabled(flag);
        deleteButton.setEnabled(flag);
    }

    public void setComboBoxItems(IndexedContainer fileList) {
        comboBox.setContainerDataSource(fileList);
    }

    public void selectFileInCombobox(String fileName) {
        comboBox.select(fileName);
    }

    public void setStatusLabelText(String text) {
        statusLabel.setValue(text);
    }

    private static String findInitialFile(FileSetup fileSetup) {
        String path = fileSetup.getSourceFile();
        if (path == null || path.isEmpty()) {
            path = fileSetup.getTargetFile();
        }
        return (path != null && !path.isEmpty()) ? path : null; 
    }*/


    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public boolean save() {
        try {
            binder.writeBean(fileSetup);
        } catch (ValidationException e) {
            logger.error("validation error while trying to save file" + e.toString());
            return false;
        }
        return true;
    }
}