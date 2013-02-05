package com.taskadapter.web.configeditor.map;

import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.converter.Converter;

/**
 * Map value property.
 * 
 * @author maxkar
 * 
 */
final class ValueProperty extends AbstractProperty {
	private static final long serialVersionUID = 1L;

	/**
	 * Mapping key.
	 */
	private final KeyProperty key;

	/**
	 * Mapping model.
	 */
	private final MapEditorModel model;

	/**
	 * Property value.
	 */
	private String value;

	/**
	 * Creates a new value property.
	 * 
	 * @param key
	 *            key property.
	 * @param model
	 *            model.
	 * @param value
	 *            used value.
	 */
	ValueProperty(KeyProperty key, MapEditorModel model, String value) {
		super();
		this.key = key;
		this.model = model;
		this.value = value;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object newValue) throws ReadOnlyException,
            Converter.ConversionException {
		this.value = newValue.toString();
		model.updateBinding((String) key.getValue());
	}

	@Override
	public Class<?> getType() {
		return String.class;
	}

}
