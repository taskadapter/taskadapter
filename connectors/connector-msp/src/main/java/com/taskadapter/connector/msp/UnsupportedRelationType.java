package com.taskadapter.connector.msp;

import com.taskadapter.model.GRelation;

public class UnsupportedRelationType extends Throwable {
    private static final long serialVersionUID = 1L;

    private final GRelation.TYPE relationType;

    public UnsupportedRelationType(GRelation.TYPE relationType) {
        this.relationType = relationType;
    }

    public GRelation.TYPE getRelationType() {
        return relationType;
    }
}
