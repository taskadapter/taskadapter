package com.taskadapter.connector.definition;

import java.util.EnumMap;
import java.util.Map;

import com.taskadapter.model.GTaskDescriptor.FIELD;

/**
 * Mappings configuration. Each mapping represents association between general
 * {@link FIELD} and internal "plugin" data field. Each such mapping may be
 * enabled or disabled. Association and "enabled" state can be managed
 * independently.
 * 
 * @author maxkar
 * 
 */
public final class Mappings {

	/**
	 * Fields mapping.
	 */
	private Map<FIELD, Mapping> fieldsMapping;

	/**
	 * Creates empty mappings.
	 */
	public Mappings() {
		this.fieldsMapping = new EnumMap<FIELD, Mapping>(FIELD.class);
	}

	/**
	 * Creates a new mapping with default values.
	 * 
	 * @param mapping
	 *            mapping with default value.
	 */
	public Mappings(Map<FIELD, Mapping> mapping) {
		this.fieldsMapping = new EnumMap<FIELD, Mapping>(FIELD.class);
		for (Map.Entry<FIELD, Mapping> entry : mapping.entrySet())
			this.fieldsMapping.put(entry.getKey(), new Mapping(entry.getValue()
					.isSelected(), entry.getValue().getCurrentValue()));
	}

	public Mappings(Mappings mapping) {
		this(mapping.fieldsMapping);
	}

	/**
	 * Checks, if field is selected for convertion.
	 * 
	 * @param field
	 *            field to check.
	 * @return <code>true</code> iff field is selected for conversion.
	 */
	public boolean isFieldSelected(FIELD field) {
		Mapping mapping = fieldsMapping.get(field);
		return (mapping != null && mapping.isSelected());
	}

	/**
	 * Returns a field "mapped" destination. If no mapping is set, returns
	 * <code>null</code>.
	 * 
	 * @param field
	 *            field.
	 * @return field, to which source is mapped.
	 */
	public String getMappedTo(FIELD field) {
		Mapping mapping = fieldsMapping.get(field);
		if (mapping != null) {
			return mapping.getCurrentValue();
		}
		return null;
	}

	/**
	 * Selects a field.
	 * 
	 * @param field
	 *            field to select.
	 */
	public void selectField(FIELD field) {
		summon(field).setSelected(true);
	}

	/**
	 * Deselects a field.
	 * 
	 * @param field
	 *            field to deselect.
	 */
	public void deselectField(FIELD field) {
		summon(field).setSelected(false);
	}

	/**
	 * Maps a field to a specified target.
	 * 
	 * @param field
	 *            GTask field.
	 * @param target
	 *            target field.
	 */
	public void setMapping(FIELD field, String target) {
		summon(field).setValue(target);
	}

	/**
	 * Sets a field "full" mapping
	 * 
	 * @param field
	 *            field to set mapping for.
	 * @param selected
	 *            "selected" state.
	 * @param target
	 *            mapping target.
	 */
	public void setMapping(FIELD field, boolean selected, String target) {
		final Mapping map = summon(field);
		map.setSelected(selected);
		map.setValue(target);
	}
	
	/**
	 * Checks presence of a mapping for a specified key.
	 * @param field field to use.
	 * @return mapped field.
	 */
	public boolean haveMappingFor(FIELD field) {
		return fieldsMapping.containsKey(field);
	}

	/**
	 * Deletes a mapping for a field.
	 * @param field field to delete a mapping for.
	 */
	public void deleteMappingFor(FIELD field) {
		fieldsMapping.remove(field);
	}
	
	/**
	 * Summons (fetches or creates) mapping for a field.
	 * 
	 * @param field
	 *            field to get a mapping for.
	 * @return field mapping.
	 */
	private Mapping summon(FIELD field) {
		final Mapping guess = fieldsMapping.get(field);
		if (guess != null)
			return guess;
		final Mapping created = new Mapping();
		fieldsMapping.put(field, created);
		return created;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fieldsMapping == null) ? 0 : fieldsMapping.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mappings other = (Mappings) obj;
		if (fieldsMapping == null) {
			if (other.fieldsMapping != null)
				return false;
		} else if (!fieldsMapping.equals(other.fieldsMapping))
			return false;
		return true;
	}

}
