package com.taskadapter.connector.common;

import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GRelation;

import java.util.List;

public interface RelationSaver {
    /**
     * Save task relations.
     *
     * @param relations relations to save.
     * @throws ConnectorException if connector fails to save a relation.
     */
    void saveRelations(List<GRelation> relations) throws ConnectorException;
}
