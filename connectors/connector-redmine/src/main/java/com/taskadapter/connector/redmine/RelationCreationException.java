package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.exceptions.EntityProcessingException;
import com.taskadapter.redmineapi.RedmineProcessingException;

/**
 * Notifies relation creation exception.
 * @author maxkar
 *
 */
public final class RelationCreationException extends EntityProcessingException {

    private static final long serialVersionUID = 1L;
    
    public RelationCreationException(RedmineProcessingException e) {
        super(
                "Can't create Tasks Relations. Note: this feature requires Redmine 1.3.0 or newer."
                        + "\nSee http://www.redmine.org/issues/7366 ."
                        + "\nThe error reported by server is: " + e.toString(),
                e);
   }

}
