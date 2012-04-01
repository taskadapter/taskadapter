package com.taskadapter.connector.msp;

import java.io.File;
import java.io.FileNotFoundException;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mspdi.MSPDIReader;
import net.sf.mpxj.reader.ProjectReader;

class MSPXMLFileReader {

    private ProjectReader projectReader;

    MSPXMLFileReader() {
        projectReader = new MSPDIReader();
    }

    public ProjectFile readFile(String projectFileName) throws FileNotFoundException, MPXJException {
        File file = new File(projectFileName);
        if (!file.exists()) {
            throw new FileNotFoundException(projectFileName);
        }

        return projectReader.read(file);
    }


}
