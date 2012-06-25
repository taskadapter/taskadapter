package com.taskadapter.connector.definition.exceptions;

/**
 * Describes an "entity processing" exceptions. This exception covers broad case
 * of causes, such as item linkage (dependency lookup), storage of an item in
 * a persistent store, etc...
 * @author maxkar
 *
 */
public class EntityProcessingException extends CommunicationException {

    private static final long serialVersionUID = 1L;

    public EntityProcessingException() {
        super();
    }

    public EntityProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityProcessingException(String message) {
        super(message);
    }

    public EntityProcessingException(Throwable cause) {
        super(cause);
    }    
}
