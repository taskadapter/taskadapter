package com.taskadapter.connector.msp;

import java.io.FileNotFoundException;
import java.io.IOException;

import net.sf.mpxj.MPXJException;

import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.EntityProcessingException;

public final class MSPExceptions {
    public static ConnectorException convertException(IOException e) {
        if (e instanceof FileNotFoundException)
            return new BadConfigException("Bad project file name: "
                    + e.getMessage(), e);
        return new EntityProcessingException(e);
    }

    public static ConnectorException convertException(MPXJException e) {
        return new EntityProcessingException(e);
    }
}
