package com.taskadapter.web.configeditor.file;

import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.web.FileManager;
import com.taskadapter.web.service.Authenticator;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Alexey Skorokhodov
 */
public class ServerModeFilePanel extends FilePanel {
    // TODO show this limit on the webpage
    public static final int MAX_FILE_SIZE_BYTES = 1000000;

    private Label status = new Label("Please select a file to upload");
    private ProgressIndicator pi = new ProgressIndicator();

    private MyReceiver receiver = new MyReceiver();
    private Upload upload = new Upload("", receiver);
    private HorizontalLayout progressLayout = new HorizontalLayout();
    // TODO use or delete
    private UploadListener uploadListener;
    private Button downloadButton;
    private Label lastModifiedLabel;
    private Authenticator authenticator;

    private MSPConfig config;

    public ServerModeFilePanel(Authenticator authenticator) {
        this.authenticator = authenticator;
        buildUI();
    }

    private void buildUI() {
        createUploadOrSelectSection();
        createDownloadSection();
    }

    private void createUploadOrSelectSection() {
        HorizontalLayout layout = new HorizontalLayout();
        createSelectFilePanel(layout);
        createUploadPanel(layout);
    }

    private void createSelectFilePanel(HorizontalLayout layout) {
        addComponent(layout);

        ComboBox comboBox = new ComboBox("Select an existing file", getContainer());
        // Sets the combobox to show a certain property as the item caption
        comboBox.setItemCaptionPropertyId("name");
        comboBox.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        comboBox.setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_STARTSWITH);
        comboBox.setImmediate(true);
        comboBox.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                Property selected = getContainer().getContainerProperty(event.getProperty().toString(), "name");
                getWindow().showNotification("Selected: " + selected);
            }
        });
        layout.addComponent(comboBox);
    }

    private void createUploadPanel(HorizontalLayout layout) {
        // Slow down the upload
        receiver.setSlow(true);

        layout.addComponent(status);
        layout.addComponent(upload);
        layout.addComponent(progressLayout);

        // Make uploading start immediately when file is selected
        upload.setImmediate(true);
        upload.setButtonCaption("Upload");

        progressLayout.setSpacing(true);
        progressLayout.setVisible(false);
        progressLayout.addComponent(pi);
        progressLayout.setComponentAlignment(pi, Alignment.MIDDLE_LEFT);

        final Button cancelProcessing = new Button("Cancel");
        cancelProcessing.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                upload.interruptUpload();
            }
        });
        cancelProcessing.setStyleName("small");
        progressLayout.addComponent(cancelProcessing);

        /**
         * =========== Add needed listener for the upload component: start,
         * progress, finish, success, fail ===========
         */

        upload.addListener(new Upload.StartedListener() {
            public void uploadStarted(StartedEvent event) {
                // This method gets called immediately after upload is started
                upload.setVisible(false);
                progressLayout.setVisible(true);
                pi.setValue(0f);
                pi.setPollingInterval(500);
                status.setValue("Uploading file \"" + event.getFilename()
                        + "\"");
            }
        });

        upload.addListener(new Upload.ProgressListener() {
            public void updateProgress(long readBytes, long contentLength) {
                // This method gets called several times during the update
                pi.setValue(new Float(readBytes / (float) contentLength));
            }

        });

        upload.addListener(new Upload.SucceededListener() {
            public void uploadSucceeded(SucceededEvent event) {
                // This method gets called when the upload finished successfully
                status.setValue("Uploading file \"" + event.getFilename()
                        + "\" succeeded");
                MyReceiver receiver1 = (MyReceiver) event.getUpload().getReceiver();
                saveFile(receiver.getFileName(), receiver1.getBytes());
                if (uploadListener != null) {
                    uploadListener.fileUploaded(receiver.getFileName());
                }
            }
        });

        upload.addListener(new Upload.FailedListener() {
            public void uploadFailed(FailedEvent event) {
                // This method gets called when the upload failed
                status.setValue("Uploading interrupted");
            }
        });

        upload.addListener(new Upload.FinishedListener() {
            public void uploadFinished(FinishedEvent event) {
                // This method gets called always when the upload finished,
                // either succeeding or failing
                progressLayout.setVisible(false);
                upload.setVisible(true);
                upload.setCaption("Select another file");
            }
        });

    }

    public IndexedContainer getContainer() {
        IndexedContainer container = new IndexedContainer();
        fillContainer(container);
        return container;
    }

    private List<File> getUserFiles() {
        String currentUser = authenticator.getUserName();
        FileManager fileManager = new FileManager();
        return fileManager.getUserFiles(currentUser);
    }

    private void fillContainer(IndexedContainer container) {
        container.addContainerProperty("id", String.class, null);
        container.addContainerProperty("name", String.class, null);
        List<File> files = getUserFiles();
        for (File file : files) {
            String name = file.getName();
            String id = file.getName();
            Item item = container.addItem(id);
            item.getItemProperty("name").setValue(name);
            item.getItemProperty("id").setValue(id);
        }
        container.sort(new Object[]{"name"},
                new boolean[]{true});
    }

    void setAvailableForDownload(File file) {
        downloadButton.setEnabled(true);
        lastModifiedLabel.setValue(new Date(file.lastModified()));
    }

    private void createDownloadSection() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        addComponent(horizontalLayout);

        downloadButton = new Button("Download file...");
        horizontalLayout.addComponent(downloadButton);
        downloadButton.setEnabled(false);
        lastModifiedLabel = new Label("Nothing to download yet");
        horizontalLayout.addComponent(lastModifiedLabel);
    }

    private void saveFile(String fileName, byte[] bytes) {
        try {
            new FileManager().saveFileOnServer(fileName, bytes);
        } catch (IOException e) {
            getWindow().showNotification("Error: " + e.toString());
        }
    }
    @Override
    public void refreshConfig(MSPConfig config) {
        System.out.println("TODO");
    }

    @Override
    public String getInputFileName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getOutputFileName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static class MyReceiver implements Upload.Receiver {

        private String fileName;
        private String mtype;
        private boolean sleep;
        private int total;
        private byte[] bytes = new byte[MAX_FILE_SIZE_BYTES];

        public OutputStream receiveUpload(String filename, String mimetype) {
            total = 0;
            fileName = filename;
            mtype = mimetype;
            return new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    bytes[total++] = (byte) b;
                    if (sleep && total % 10000 == 0) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if (total == MAX_FILE_SIZE_BYTES) {
                        throw new RuntimeException("Max file size reached: " + MAX_FILE_SIZE_BYTES + " bytes");
                    }
                }
            };
        }

        public String getFileName() {
            return fileName;
        }

        public String getMimeType() {
            return mtype;
        }

        public void setSlow(boolean value) {
            sleep = value;
        }

        public byte[] getBytes() {
            return Arrays.copyOf(bytes, total);
        }
    }

}
