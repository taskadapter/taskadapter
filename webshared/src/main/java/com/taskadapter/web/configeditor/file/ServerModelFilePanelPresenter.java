package com.taskadapter.web.configeditor.file;

import com.taskadapter.web.FileManager;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Upload;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Author: Alexander Kulik
 * Date: 10.05.12 23:51
 */
public class ServerModelFilePanelPresenter {
    public static final int MAX_FILE_SIZE_BYTES = 1000000;
    private final String userName;

    private IndexedContainer fileList;
    private ServerModeFilePanel2 view;
    private File selectedFile;
    private final UploadReceiver uploadReceiver;

    public ServerModelFilePanelPresenter(String userName) {
        this.userName = userName;
        fileList = buildFileList();
        uploadReceiver = new UploadReceiver();
        uploadReceiver.setSlow(true);
    }

    /**
     * Create sorted list of uploaded and exported files for current user
     *
     * @return list
     */
    private IndexedContainer buildFileList() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(ServerModeFilePanel2.COMBOBOX_ITEM_PROPERTY, String.class, null);
        List<File> files = new FileManager().getUserFiles(userName);
        for (File file : files) {
            addFileToContainer(container, file.getName());
        }
        container.sort(new Object[]{ServerModeFilePanel2.COMBOBOX_ITEM_PROPERTY},
                new boolean[]{true});

        return container;
    }

    private static void addFileToContainer(IndexedContainer container, String fileName) {
        Item item = container.addItem(fileName);
        item.getItemProperty(ServerModeFilePanel2.COMBOBOX_ITEM_PROPERTY).setValue(fileName);
    }


    public IndexedContainer getFileList() {
        return fileList;
    }

    public void setView(ServerModeFilePanel2 view) {
        this.view = view;
        view.setComboBoxItems(fileList);
        onNoFileSelected();
    }

    public void onFileSelected(String fileName) {
        selectedFile = new FileManager().getFileForUser(userName, fileName);
        File file = getSelectedFile();
        SimpleDateFormat sdf = new SimpleDateFormat(ServerModeFilePanel2.DATE_FORMAT, Locale.US);
        view.setDateLabelText(sdf.format(file.lastModified()));
        view.setDownloadEnabled(true);
    }

    public void onNoFileSelected() {
        this.selectedFile = null;
        view.setDateLabelText("");
        view.setDownloadEnabled(false);
    }

    private File getSelectedFile() {
        return selectedFile;
    }

    public void downloadSelectedFile() {
        if (selectedFile == null) {
            view.showNotification(ServerModeFilePanel2.DOWNLOAD_FILE_ERROR);
            return;
        }
        FileDownloadResource resource = new FileDownloadResource(getSelectedFile(), view.getApplication());
        view.getWindow().open(resource);
    }

    public UploadReceiver getUploadReceiver() {
        return uploadReceiver;
    }

    /**
     * This method gets called immediately after upload is started
     */
    public void uploadStarted(Upload.StartedEvent event) {
        view.setUploadStatusText(ServerModeFilePanel2.UPLOADING);
        view.setUploadEnabled(false);
    }

    /**
     * This method gets called when the upload finished successfully
     */
    public void uploadSucceeded(Upload.SucceededEvent event) {
        if (uploadReceiver.saveFile(userName)) {
            if (fileList.getItem(uploadReceiver.getFileName()) == null) {
                addFileToContainer(fileList, uploadReceiver.getFileName());
                view.setComboBoxItems(fileList);
            }
            view.selectFileInCombobox(uploadReceiver.getFileName());
            view.setUploadStatusText(ServerModeFilePanel2.UPLOAD_SUCCESS);
        } else {
            view.setUploadStatusText(ServerModeFilePanel2.SAVE_FILE_FAILED);
        }

        view.setUploadEnabled(true);
    }

    /**
     * This method gets called when the upload failed
     */
    public void uploadFailed(Upload.FailedEvent event) {
        view.setUploadEnabled(true);
        view.setUploadStatusText(ServerModeFilePanel2.UPLOAD_FAILED);
    }
}
