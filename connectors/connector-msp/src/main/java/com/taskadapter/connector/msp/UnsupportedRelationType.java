package com.taskadapter.connector.msp;

import com.taskadapter.model.RelationType;

public class UnsupportedRelationType extends Throwable {
    private static final long serialVersionUID = 1L;

    private final RelationType relationType;

    public UnsupportedRelationType(RelationType relationType) {
        this.relationType = relationType;
    }

    public RelationType getRelationType() {
        return relationType;
    }
}
