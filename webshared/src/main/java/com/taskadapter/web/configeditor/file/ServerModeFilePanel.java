package com.taskadapter.web.configeditor.file;

import com.taskadapter.web.MessageDialog;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Runo;

import java.io.File;
import java.util.Arrays;

public class ServerModeFilePanel extends Panel{

    // TODO show this limit on the webpage
    static final int MAX_FILE_SIZE_BYTES = 5000000;

    private static final String TITLE = "Microsoft Project file";
    private static final String COMBOBOX_INPUT_PROMPT = "Select an existing file";
    public static final String FILE_WILL_GENERATED_HINT = "File will be auto-created on export";

    static final String COMBOBOX_ITEM_PROPERTY = "name";
    static final String DATE_FORMAT = "d MMM yyyy h:mm:ss a z";
    static final String DOWNLOAD_BUTTON_CAPTION = "Download file";
    private static final String UPLOAD_BUTTON_CAPTION = "Upload new";
    static final String UPLOAD_FAILED = "Upload failed";
    static final String UPLOADING = "Uploading";
    private static final String DELETE_BUTTON_CAPTION = "Delete";
    private static final String QUESTION_DELETE_FILE = "Delete selected file?";
    private static final String CONFIRMATION_DIALOG_TITLE = "Deleting";
    private static final String CONFIRMATION_DIALOG_DELETE_BUTTON = "Delete";
    static final String FILE_DELETED_SUCCESS = "File deleted";
    static final String FILE_DELETED_FAILED = "File deletion error";

    private static final int DELETE_FILE_ACTION = 1;
    private static final String COMBOBOX_WIDTH = "175px";
    private static final String CONFIRMATION_DIALOG_WIDTH = "200px";


    private Label statusLabel;
    private final ServerModelFilePanelPresenter presenter;
    private Upload uploadButton;
    private Button downloadButton;
    private ComboBox comboBox;
    private ProgressIndicator progressIndicator;
    private Button deleteButton;
    private final Property<String> inputFilePath;
    private final Property<String> outputFilePath;
    
    public ServerModeFilePanel(File filesDirectory, Property<String> inputFilePath,
            Property<String> outputFilePath, UploadProcessor uploadProcessor) {
    super(TITLE);        
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
        this.presenter = new ServerModelFilePanelPresenter(filesDirectory,
                uploadProcessor);
        buildUI();
        presenter.setView(this);
        presenter.init(findInitialFile(inputFilePath, outputFilePath));
    }

    private void buildUI() {
        VerticalLayout view = new VerticalLayout();
        view.addComponent(createComboboxPanel());
        view.addComponent(createDateLabelPanel());
        view.addComponent(createUploadPanel());
        setContent(view);
    }

    private Layout createComboboxPanel() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.addComponent(createComboBox());
        layout.addComponent(createDownloadButton());
        layout.addComponent(createDeleteButton());
        return layout;
    }

    private Layout createDateLabelPanel() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);

        layout.addComponent(new Label("&nbsp;&nbsp;&nbsp;", ContentMode.HTML));

        statusLabel = new Label("", ContentMode.HTML);
        statusLabel.setStyleName(Runo.LABEL_SMALL);
        layout.addComponent(statusLabel);
        layout.setComponentAlignment(statusLabel, Alignment.MIDDLE_LEFT);

        progressIndicator = new ProgressIndicator(new Float(0.0));
        progressIndicator.setPollingInterval(500);
        progressIndicator.setVisible(false);
        layout.addComponent(progressIndicator);
        layout.setComponentAlignment(progressIndicator, Alignment.MIDDLE_LEFT);

        return layout;
    }

    private Layout createUploadPanel() {
        Layout layout = new VerticalLayout();

        layout.addComponent(new Label("<hr>", ContentMode.HTML));

        Layout bottomToolLayout = new HorizontalLayout();
        bottomToolLayout.addComponent(createUploadButton());

        layout.addComponent(bottomToolLayout);

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
        deleteButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                showConfirmationDialog(QUESTION_DELETE_FILE, DELETE_FILE_ACTION);

            }
        });
        return deleteButton;
    }

    private void showConfirmationDialog(String question, final int action) {
        MessageDialog messageDialog = new MessageDialog(
                CONFIRMATION_DIALOG_TITLE, question,
                Arrays.asList(CONFIRMATION_DIALOG_DELETE_BUTTON, MessageDialog.CANCEL_BUTTON_LABEL),
                new MessageDialog.Callback() {
                    public void onDialogResult(String answer) {
                        if (!answer.equals(MessageDialog.CANCEL_BUTTON_LABEL)) {
                            if (action == DELETE_FILE_ACTION) {
                                presenter.deleteSelectedFile();
                            }
                        }
                    }
                }
        );
        messageDialog.setWidth(CONFIRMATION_DIALOG_WIDTH);
        getUI().addWindow(messageDialog);
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
                inputFilePath.setValue(presenter.getSelectedFileNameOrEmpty());
                outputFilePath.setValue(presenter.getSelectedFileNameOrEmpty());
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

    private static String findInitialFile(Property inputFilePath,
            Property outputFilePath) {
        String path = (String) inputFilePath.getValue();
        if (path == null || path.isEmpty()) {
            path = (String) outputFilePath.getValue();
        }
        return (path != null && !path.isEmpty()) ? path : null; 
    }
}