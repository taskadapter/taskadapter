package com.taskadapter.web.configeditor.file;

import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.web.MessageDialog;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Runo;

import java.util.Arrays;

/**
 * @author Alexander Kulik
 */
public class ServerModeFilePanel extends FilePanel {

    // TODO show this limit on the webpage
    static final int MAX_FILE_SIZE_BYTES = 5000000;

    private static final String TITLE = "Microsoft Project file";
    private static final String COMBOBOX_INPUT_PROMPT = "Select an existing file";
    static final String COMBOBOX_ITEM_PROPERTY = "name";
    static final String DATE_FORMAT = "d MMM yyyy h:mm:ss a z";
    static final String DOWNLOAD_BUTTON_CAPTION = "Download file";
    private static final String UPLOAD_BUTTON_CAPTION = "Upload new";
    static final String DOWNLOAD_FILE_ERROR = "Download file error";
    static final String UPLOAD_FAILED = "Upload failed";
    static final String UPLOAD_SUCCESS = "Upload success";
    static final String SAVE_FILE_FAILED = "Save file error"; // error of saving after upload
    static final String UPLOADING = "Uploading";
    private static final String DELETE_BUTTON_CAPTION = "Delete";
    private static final String QUESTION_DELETE_FILE = "Delete selected file?";
    private static final String CONFIRMATION_DIALOG_TITLE = "Deleting";
    private static final String CONFIRMATION_DIALOG_DELETE_BUTTON = "Delete";
    private static final String CONFIRMATION_DIALOG_CANCEL_BUTTON = "Cancel";
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
    public static final String UPLOAD_MPP_SUCCESS = "File uploaded and successfully converted to XML";
    public static final String CANNOT_DELETE_MPP_FILE = "Cannot delete .mpp file";

    public ServerModeFilePanel(ServerModelFilePanelPresenter presenter) {
        super(TITLE);
        this.presenter = presenter;
        buildUI();
        presenter.setView(this);
    }

    private void buildUI() {
        removeAllComponents();

        addComponent(createComboboxPanel());
        addComponent(createDateLabelPanel());
        addComponent(createUploadPanel());
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

        layout.addComponent(new Label("&nbsp;&nbsp;&nbsp;", Label.CONTENT_XHTML));

        statusLabel = new Label("", Label.CONTENT_XHTML);
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

        layout.addComponent(new Label("<hr>", Label.CONTENT_XHTML));

        Layout bottomToolLayout = new HorizontalLayout();
        bottomToolLayout.addComponent(createUploadButton());

        layout.addComponent(bottomToolLayout);

        return layout;
    }

    private Component createUploadButton() {
        uploadButton = new Upload();
        uploadButton.setReceiver(presenter.getUploadReceiver());
        uploadButton.setImmediate(true);
        uploadButton.setButtonCaption(UPLOAD_BUTTON_CAPTION);

        uploadButton.addListener(Upload.StartedEvent.class, presenter, "uploadStarted");
        uploadButton.addListener(Upload.SucceededEvent.class, presenter, "uploadSucceeded");
        uploadButton.addListener(Upload.FailedEvent.class, presenter, "uploadFailed");
        uploadButton.addListener(new Upload.ProgressListener() {
            public void updateProgress(long readBytes, long contentLength) {
                progressIndicator.setValue(new Float(readBytes / (float) contentLength));
            }
        });

        return uploadButton;
    }

    private Component createDeleteButton() {
        deleteButton = new Button(DELETE_BUTTON_CAPTION);
        deleteButton.addListener(new Button.ClickListener() {
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
                Arrays.asList(CONFIRMATION_DIALOG_DELETE_BUTTON, CONFIRMATION_DIALOG_CANCEL_BUTTON),
                new MessageDialog.Callback() {
                    public void onDialogResult(String answer) {
                        if (!answer.equals(CONFIRMATION_DIALOG_CANCEL_BUTTON)) {
                            if (action == DELETE_FILE_ACTION) {
                                presenter.deleteSelectedFile();
                            }
                        }
                    }
                }
        );
        messageDialog.setWidth(CONFIRMATION_DIALOG_WIDTH);
        getApplication().getMainWindow().addWindow(messageDialog);
    }


    private Component createComboBox() {
        comboBox = new ComboBox();
        comboBox.setItemCaptionPropertyId(COMBOBOX_ITEM_PROPERTY);// Sets the combobox to show a certain property as the item caption
        comboBox.setWidth(COMBOBOX_WIDTH);
        comboBox.setNewItemsAllowed(false);
        comboBox.setInputPrompt(COMBOBOX_INPUT_PROMPT);

        comboBox.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        comboBox.setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_STARTSWITH);
        comboBox.setImmediate(true);
        comboBox.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                Property selected = presenter.getFileList().getContainerProperty(event.getProperty().toString(), COMBOBOX_ITEM_PROPERTY);
                if (selected != null) {
                    presenter.onFileSelected(selected.toString());
                } else {
                    presenter.onNoFileSelected();
                }
            }
        });
        return comboBox;
    }

    private Component createDownloadButton() {
        downloadButton = new Button(DOWNLOAD_BUTTON_CAPTION);
        downloadButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.downloadSelectedFile();
            }
        });
        return downloadButton;
    }

    @Override
    public void refreshConfig(MSPConfig config) {
        presenter.setConfig(config);
    }

    @Override
    public String getInputFileName() {
        return presenter.getSelectedFileNameOrNull();
    }

    @Override
    public String getOutputFileName() {
        return presenter.getSelectedFileNameOrNull();
    }

    public void setUploadEnabled(boolean flag) {
        // upload disabled in case of uploading process
        progressIndicator.setVisible(!flag);
        if (flag) {
            progressIndicator.setValue(0);
        }

        uploadButton.setEnabled(flag);
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

    public void showNotification(String message) {
        getWindow().showNotification(message);
    }
}

