package com.taskadapter.web.configeditor.file;

import com.vaadin.server.DownloadStream;
import com.vaadin.server.FileResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileDownloadResource extends FileResource {

    public FileDownloadResource(File sourceFile) {
        super(sourceFile);
    }

    public DownloadStream getStream() {
        try {
            final DownloadStream ds = new DownloadStream(
                    new FileInputStream(getSourceFile()), getMIMEType(),
                    getFilename());
            ds.setParameter("Content-Disposition", "attachment; filename=" + getFilename());
            ds.setCacheTime(getCacheTime());
            return ds;
        } catch (final FileNotFoundException e) {
            // No logging for non-existing files at this level.
            return null;
        }
    }
}
