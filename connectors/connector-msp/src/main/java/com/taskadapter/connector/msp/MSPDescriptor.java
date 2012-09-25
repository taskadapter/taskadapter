package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.Descriptor;

public class MSPDescriptor {
    public static final Descriptor instance = new Descriptor(MSPConnector.ID, MSPConfig.DEFAULT_LABEL);
}
