package com.taskadapter.connector.msp.write;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.writer.ProjectWriter;

import java.io.File;
import java.io.IOException;

public class RealWriter {
    /**
     * This method will create all required parent folders if needed.
     *
     * @param project project
     * @return absolute file path
     * @throws java.io.IOException exception
     */
    public static String writeProject(String absoluteFilePath, ProjectFile project)
            throws IOException {
        File folder = new File(absoluteFilePath).getParentFile();
        if (folder != null) {
            folder.mkdirs();
        }
        File realFile = new File(absoluteFilePath);

        ProjectWriter writer = new MSPDIWriter();
        writer.write(project, realFile);
        return realFile.getAbsolutePath();
    }

}
