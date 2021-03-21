package com.taskadapter.web.configeditor.file;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

public final class ServerModelFilePanelPresenter {
    public static final int MAX_FILE_SIZE_BYTES = 5000000;

//    private IndexedContainer fileList;
    private ServerModeFilePanel view;
    private File selectedFile;
    private final UploadReceiver uploadReceiver;    
    private final File userContentDirectory;
    private final UploadProcessor uploadProcessor;
//    private final FileDownloader downloader;

    public ServerModelFilePanelPresenter(File userContentDirectory, UploadProcessor uploadProcessor) {
        this.userContentDirectory = userContentDirectory;
        this.uploadProcessor = uploadProcessor;
//        fileList = buildFileList();
        uploadReceiver = new UploadReceiver(userContentDirectory);
//        FileDownloadResource resource = new FileDownloadResource(new File("dummyfile"));
//        downloader = new FileDownloader(resource);
    }

    /**
     * TODO !!! see deprecated.
     * @deprecated remove this spahetti!!!
     */
    @Deprecated
    void init(String initialFile) {
        if (initialFile != null) {
//            view.selectFileInCombobox(FileUtils.basename(initialFile));
        } else {
            onNoFileSelected();
        }
    }

    /**
     * Create sorted list of uploaded and exported files for current user
     */
/*    private IndexedContainer buildFileList() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(ServerModeFilePanel.COMBOBOX_ITEM_PROPERTY, String.class, null);
        
        final File[] files = userContentDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                addFileToContainer(container, file.getName());
            }
        }
        
        container.sort(new Object[]{ServerModeFilePanel.COMBOBOX_ITEM_PROPERTY},
                new boolean[]{true});

        return container;
    }*/

/*    private static void addFileToContainer(IndexedContainer container, String fileName) {
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
        downloader.extend(view.getDownloadButton());
    }*/

    public void onFileSelected(String fileName) {
        if (fileName.isEmpty()) {
            onNoFileSelected();
            return;
        }

        selectedFile = new File(userContentDirectory,
                FileUtils.basename(fileName));
        SimpleDateFormat sdf = new SimpleDateFormat(ServerModeFilePanel.DATE_FORMAT, Locale.US);
//        view.setStatusLabelText(sdf.format(selectedFile.lastModified()));
//        downloader.setFileDownloadResource(new FileDownloadResource(selectedFile));
//        view.setDownloadEnabled(true);
    }

    public void onNoFileSelected() {
        this.selectedFile = null;
//        view.setStatusLabelText(ServerModeFilePanel.FILE_WILL_GENERATED_HINT);
//        view.setDownloadEnabled(false);
    }

    public UploadReceiver getUploadReceiver() {
        return uploadReceiver;
    }

    /**
     * This method gets called immediately after upload is started
     */
/*
    public void uploadStarted(Upload.StartedEvent event) {
        view.setStatusLabelText(ServerModeFilePanel.UPLOADING);
        view.setUploadEnabled(false);
    }
*/

    /**
     * This method gets called when the upload finished successfully
     */
/*
    public void uploadSucceeded(Upload.SucceededEvent event) {
        final File uploaded = new File(userContentDirectory,
                FileUtils.basename(event.getFilename()));
        final FileProcessingResult res = uploadProcessor.processFile(uploaded);
        if (res.getResultFile() != null) {
            addFileToComboBoxAndSelect(res.getResultFile().getName());
        }
//        view.setStazztusLabelText(res.getMessage());
        Notification.show("", res.getMessage(), Notification.Type.TRAY_NOTIFICATION);
        view.setUploadEnabled(true);
    }

    private void addFileToComboBoxAndSelect(String fileName) {
        if (fileList.getItem(fileName) == null) {
            addFileToContainer(fileList, fileName);
            view.setComboBoxItems(fileList);
        }
        view.selectFileInCombobox(fileName);
    }

*/
    /**
     * This method gets called when the upload failed
     */
/*    public void uploadFailed(Upload.FailedEvent event) {
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
    }*/

    public String getSelectedFileNameOrEmpty() {
        return selectedFile != null ? selectedFile.getAbsolutePath() : "";
    }
}
