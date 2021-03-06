package com.taskadapter.connector.msp;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.ProjectReaderUtility;

import java.io.File;
import java.io.FileNotFoundException;

public class MSPFileReader {
    public static final String MPP_SUFFIX_LOWERCASE = ".mpp";

    public ProjectFile readFile(String projectFileName) throws FileNotFoundException, MPXJException {
        File file = new File(projectFileName);
        if (!file.exists()) {
            throw new FileNotFoundException(projectFileName);
        }
        ProjectReader projectReader = ProjectReaderUtility.getProjectReader(projectFileName);
        return projectReader.read(file);
    }
}
