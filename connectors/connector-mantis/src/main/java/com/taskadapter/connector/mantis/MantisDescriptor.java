package com.taskadapter.connector.mantis;

import com.taskadapter.connector.definition.Descriptor;

public class MantisDescriptor {
    public static final Descriptor instance = new Descriptor(MantisConnector.ID, MantisConfig.DEFAULT_LABEL);
}
