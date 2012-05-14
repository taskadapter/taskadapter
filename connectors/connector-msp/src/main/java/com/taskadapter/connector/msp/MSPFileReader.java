package com.taskadapter.connector.msp;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mspdi.MSPDIReader;
import net.sf.mpxj.reader.ProjectReader;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Alexey Skorokhodov
 */
public class MSPFileReader {
    public static final String XML_SUFFIX_LOWERCASE = ".xml";
    public static final String MPP_SUFFIX_LOWERCASE = ".mpp";

    public ProjectFile readFile(String projectFileName) throws FileNotFoundException, MPXJException {
        File file = new File(projectFileName);
        if (!file.exists()) {
            throw new FileNotFoundException(projectFileName);
        }

        ProjectReader projectReader;
        if (projectFileName.toLowerCase().endsWith(MPP_SUFFIX_LOWERCASE)) {
            projectReader = new MPPReader();
        } else {
            projectReader = new MSPDIReader();
        }
        return projectReader.read(file);
    }
}
