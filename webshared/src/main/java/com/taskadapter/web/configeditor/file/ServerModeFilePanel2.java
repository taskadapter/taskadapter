package com.taskadapter.web.configeditor.file;

import com.taskadapter.connector.msp.MSPConfig;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Runo;

/**
 * @author Alexander Kulik
 */
public class ServerModeFilePanel2 extends FilePanel {

    // TODO show this limit on the webpage
    static final int MAX_FILE_SIZE_BYTES = 5000000;

    private static final String TITLE = "Microsoft Project file";
    private static final String COMBOBOX_INPUT_PROMPT = "Select an existing file";
    public static final String COMBOBOX_ITEM_PROPERTY = "name";
    public static final String DATE_FORMAT = "d MMM yyyy h:mm:ss a z";
    private static final String DOWNLOAD_BUTTON_CAPTION = "Download file";
    private static final String UPLOAD_BUTTON_CAPTION = "Upload new";
    public static final String DOWNLOAD_FILE_ERROR = "Download file error";
    public static final String UPLOAD_FAILED = "Upload failed";
    public static final String UPLOAD_SUCCESS = "Upload success";
    public static final String SAVE_FILE_FAILED = "Save file error"; // error of saving after upload
    public static final String UPLOADING = "Uploading...";


    private Label dateLabel;
    private final ServerModelFilePanelPresenter presenter;
    private Upload uploadButton;
    private Button downloadButton;
    private ComboBox comboBox;
    private Label uploadStatusLabel;

    public ServerModeFilePanel2(ServerModelFilePanelPresenter presenter) {
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
        return layout;
    }

    private Layout createDateLabelPanel() {
        Layout layout = new HorizontalLayout();

        layout.addComponent(new Label("&nbsp;&nbsp;&nbsp;", Label.CONTENT_XHTML));

        dateLabel = new Label("", Label.CONTENT_XHTML);
        dateLabel.setStyleName(Runo.LABEL_SMALL);
        layout.addComponent(dateLabel);

        return layout;
    }

    private Layout createUploadPanel() {
        Layout layout = new VerticalLayout();
        layout.addComponent(new Label("<hr>", Label.CONTENT_XHTML));

        Layout line = new HorizontalLayout();
        uploadButton = new Upload();
        uploadButton.setReceiver(presenter.getUploadReceiver());
        uploadButton.setImmediate(true);
        uploadButton.setButtonCaption(UPLOAD_BUTTON_CAPTION);
        //uploadButton.setStyleName("myCustomUpload");

        uploadButton.addListener(Upload.StartedEvent.class, presenter, "uploadStarted");
        uploadButton.addListener(Upload.SucceededEvent.class, presenter, "uploadSucceeded");
        uploadButton.addListener(Upload.FailedEvent.class, presenter, "uploadFailed");
        line.addComponent(uploadButton);
        
        uploadStatusLabel = new Label();
        //uploadStatusLabel.setStyleName(Runo.LABEL_SMALL);
        line.addComponent(uploadStatusLabel);

        layout.addComponent(line);
        return layout;
    }

    private Component createComboBox() {
        comboBox = new ComboBox();
        comboBox.setItemCaptionPropertyId(COMBOBOX_ITEM_PROPERTY);// Sets the combobox to show a certain property as the item caption
        comboBox.setWidth("250px");
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
        //HorizontalLayout layout = new HorizontalLayout();

        downloadButton = new Button(DOWNLOAD_BUTTON_CAPTION);
        //downloadButton.setStyleName(Runo.BUTTON_SMALL);
        downloadButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.downloadSelectedFile();
            }
        });
        //layout.addComponent(downloadButton);

        return downloadButton;
    }

    @Override
    public void refreshConfig(MSPConfig config) {

    }

    @Override
    public String getInputFileName() {
        return null;
    }

    @Override
    public String getOutputFileName() {
        return null;
    }

    public void setDateLabelText(String text) {
        dateLabel.setValue(text);
    }

    public void showNotification(String message) {
        getWindow().showNotification(message);
    }
    
    public void setUploadEnabled(boolean flag) {
        uploadButton.setEnabled(flag);
    }

    public void setDownloadEnabled(boolean flag) {
        downloadButton.setEnabled(flag);
    }

    public void setComboBoxItems(IndexedContainer fileList) {
        comboBox.setContainerDataSource(fileList);
    }

    public void selectFileInCombobox(String fileName) {
        comboBox.select(fileName);
    }

    public void setUploadStatusText(String text) {
        uploadStatusLabel.setValue(text);
    }
}

