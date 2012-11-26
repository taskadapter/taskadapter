package com.taskadapter.web.ui;

import com.vaadin.data.Property;

/**
 * Input field with autocompletion possibilities.
 * 
 * @param <C>
 *            completion type.
 */
public interface CompletableInput<C> {
    /**
     * Property with current field value. This property may be absolutely
     * read-only.
     * 
     * @return property holding current field value.
     */
    public Property value();

    /**
     * List of possible completion itmes in current state. Does not perform
     * filtering for a current input. This property may be absolutely read-only.
     * Real type of this property is Property<List<C>>.
     * 
     * @return property with current completions.
     */
    public Property completions();

    /**
     * Handles a free keyboard/mouse input. This method MAY be called each time
     * user types a letter on a keyboard.
     * 
     * @param input
     *            current user input value.
     */
    public void freeInput(String input);

    /**
     * Handles a selection of a completion item.
     * 
     * @param value
     *            completion item selected by a user.
     */
    public void selection(C value);
}
