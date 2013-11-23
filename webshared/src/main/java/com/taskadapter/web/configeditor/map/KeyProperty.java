package com.taskadapter.web.configeditor.map;

import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.converter.Converter;

/**
 * Map "key" property.
 * 
 */
final class KeyProperty extends AbstractProperty<String> {
	private static final long serialVersionUID = 1L;

	/**
	 * Used key.
	 */
	private String key;

	/**
	 * Used model.
	 */
	private final MapEditorModel model;

	/**
	 * Creates a new key property.
	 * 
	 * @param key
	 * @param model
	 */
	KeyProperty(String key, MapEditorModel model) {
		super();
		this.key = key;
		this.model = model;
	}

	@Override
	public String getValue() {
		return key;
	}

	@Override
	public void setValue(String newValue) throws ReadOnlyException,
            Converter.ConversionException {
		final String oldKey = this.key;
		final String newKey = newValue;
		if (oldKey.equals(newKey))
			return;
		this.key = newKey;
		
		model.updateBinding(oldKey, newKey);
	}

	@Override
	public Class<String> getType() {
		return String.class;
	}

}
