package com.taskadapter.connector.definition.exceptions;

/**
 * Entity persistence exception. This exception is thrown when there are
 * problems with an entity persistent storage.
 * 
 * @author maxkar
 * 
 */
public class EntityPersistenseException extends EntityProcessingException {
    private static final long serialVersionUID = 1L;

    public EntityPersistenseException() {
        super();
    }

    public EntityPersistenseException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityPersistenseException(String message) {
        super(message);
    }

    public EntityPersistenseException(Throwable cause) {
        super(cause);
    }

}
