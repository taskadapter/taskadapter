package com.taskadapter.connector.common;

import java.util.List;

import com.taskadapter.connector.definition.exceptions.ConnectorException;

import com.taskadapter.model.GRelation;

/**
 * Basic relation saver.
 * 
 */
public interface RelationSaver {
    /**
     * Saves a relations.
     * 
     * @param relations
     *            relations to save.
     * @throws ConnectorException
     *             if connector fails to save a relation.
     */
    void saveRelations(List<GRelation> relations)
            throws ConnectorException;

}
