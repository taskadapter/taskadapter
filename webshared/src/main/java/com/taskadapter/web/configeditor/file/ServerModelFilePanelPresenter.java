package com.taskadapter.web.configeditor.file;

import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPFileReader;
import com.taskadapter.connector.msp.MSPUtils;
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
    private ServerModeFilePanel view;
    private File selectedFile;
    private final UploadReceiver uploadReceiver;
    private FileManager fileManager = new FileManager();

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
        container.addContainerProperty(ServerModeFilePanel.COMBOBOX_ITEM_PROPERTY, String.class, null);
        List<File> files = new FileManager().getUserFiles(userName);
        for (File file : files) {
            addFileToContainer(container, file.getName());
        }
        container.sort(new Object[]{ServerModeFilePanel.COMBOBOX_ITEM_PROPERTY},
                new boolean[]{true});

        return container;
    }

    private static void addFileToContainer(IndexedContainer container, String fileName) {
        Item item = container.addItem(fileName);
        item.getItemProperty(ServerModeFilePanel.COMBOBOX_ITEM_PROPERTY).setValue(fileName);
    }

    private static void removeFileFromContainer(IndexedContainer container, String fileName) {
        container.removeItem(fileName);
    }

    public IndexedContainer getFileList() {
        return fileList;
    }

    public void setView(ServerModeFilePanel view) {
        this.view = view;
        view.setComboBoxItems(fileList);
        onNoFileSelected();
    }

    public void onFileSelected(String fileName) {
        if (fileName.isEmpty()) {
            onNoFileSelected();
            return;
        }

        selectedFile = fileManager.getFileForUser(userName, fileName);
        File file = getSelectedFile();
        SimpleDateFormat sdf = new SimpleDateFormat(ServerModeFilePanel.DATE_FORMAT, Locale.US);
        view.setStatusLabelText(sdf.format(file.lastModified()));
        view.setDownloadEnabled(true);
    }

    public void onNoFileSelected() {
        this.selectedFile = null;
        view.setStatusLabelText("");
        view.setDownloadEnabled(false);
    }

    private File getSelectedFile() {
        return selectedFile;
    }

    public void downloadSelectedFile() {
        if (selectedFile == null) {
            view.showNotification(ServerModeFilePanel.DOWNLOAD_FILE_ERROR);
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
     * @param event event
     */
    @SuppressWarnings("UnusedDeclaration")
    public void uploadStarted(Upload.StartedEvent event) {
        view.setStatusLabelText(ServerModeFilePanel.UPLOADING);
        view.setUploadEnabled(false);
    }

    /**
     * This method gets called when the upload finished successfully
     * @param event event
     */
    @SuppressWarnings("UnusedDeclaration")
    public void uploadSucceeded(Upload.SucceededEvent event) {
        if (uploadReceiver.saveFile(userName)) {
            String fileName = uploadReceiver.getFileName();
            
            // check if MPP file
            boolean isMpp = fileName.toLowerCase().endsWith(MSPFileReader.MPP_SUFFIX_LOWERCASE);
            if (isMpp) {
                File f = fileManager.getFileForUser(userName, fileName);
                String newFilePath = MSPUtils.moveMppProjectFileToXml(f.getAbsolutePath());
                if (newFilePath == null) {
                    // move error
                    view.setStatusLabelText(ServerModeFilePanel.SAVE_FILE_FAILED);
                    view.setUploadEnabled(true);
                    return;
                }

                if (!f.delete()) {
                    view.showNotification(ServerModeFilePanel.CANNOT_DELETE_MPP_FILE);
                }
                fileName = new File(newFilePath).getName();
            }
           
            // add to ComboBox
            if (fileList.getItem(fileName) == null) {
                addFileToContainer(fileList, fileName);
                view.setComboBoxItems(fileList);
            }

            view.selectFileInCombobox(fileName);
            view.setStatusLabelText(isMpp? ServerModeFilePanel.UPLOAD_MPP_SUCCESS : ServerModeFilePanel.UPLOAD_SUCCESS);
        } else {
            view.setStatusLabelText(ServerModeFilePanel.SAVE_FILE_FAILED);
        }

        view.setUploadEnabled(true);
    }

    /**
     * This method gets called when the upload failed
     * @param event event
     */
    @SuppressWarnings("UnusedDeclaration")
    public void uploadFailed(Upload.FailedEvent event) {
        view.setUploadEnabled(true);
        view.setStatusLabelText(ServerModeFilePanel.UPLOAD_FAILED);
    }

    public void deleteSelectedFile() {
        if (selectedFile.delete()) {
            removeFileFromContainer(fileList, selectedFile.getName());
            view.setComboBoxItems(fileList);
            view.selectFileInCombobox(null);
            view.setStatusLabelText(ServerModeFilePanel.FILE_DELETED_SUCCESS);
        } else {
            view.setStatusLabelText(ServerModeFilePanel.FILE_DELETED_FAILED);
        }
    }

    public String getSelectedFileNameOrNull() {
        return selectedFile != null ? selectedFile.getName() : null;
    }

    public void setConfig(MSPConfig config) {
        String fileName = config.getInputFileName();
        view.selectFileInCombobox(fileName);
    }
}
