package com.taskadapter.connector.msp;

import com.taskadapter.model.GRelationType;

public class UnsupportedRelationType extends Throwable {
    private static final long serialVersionUID = 1L;

    private final GRelationType relationType;

    public UnsupportedRelationType(GRelationType relationType) {
        this.relationType = relationType;
    }

    public GRelationType getRelationType() {
        return relationType;
    }
}
