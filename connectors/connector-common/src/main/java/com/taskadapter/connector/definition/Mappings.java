package com.taskadapter.connector.definition;

import java.util.HashMap;
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
	 * Selected fields.
	 */
	private final Map<FIELD, Boolean> selected;

	/**
	 * "Map item to" setting.
	 */
	private final Map<FIELD, String> mapTo;

	/**
	 * Creates empty mappings.
	 */
	public Mappings() {
		this.selected = new HashMap<FIELD, Boolean>();
		this.mapTo = new HashMap<FIELD, String>();
	}

	/**
	 * Copy constructor for mapping.
	 * 
	 * @param mapping
	 *            new mapping.
	 */
	public Mappings(Mappings mapping) {
		this.selected = new HashMap<FIELD, Boolean>(mapping.selected);
		this.mapTo = new HashMap<FIELD, String>(mapping.mapTo);
	}

	/**
	 * Checks, if field is selected for convertion.
	 * 
	 * @param field
	 *            field to check.
	 * @return <code>true</code> iff field is selected for conversion.
	 */
	public boolean isFieldSelected(FIELD field) {
		final Boolean result = selected.get(field);
		return result != null && result.booleanValue();
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
		return mapTo.get(field);
	}

	/**
	 * Selects a field.
	 * 
	 * @param field
	 *            field to select.
	 */
	public void selectField(FIELD field) {
		setFieldSelected(field, true);
	}

	/**
	 * Deselects a field.
	 * 
	 * @param field
	 *            field to deselect.
	 */
	public void deselectField(FIELD field) {
		setFieldSelected(field, false);
	}

	/**
	 * Sets a "selected" value for a field.
	 * 
	 * @param filed
	 *            field to set value to.
	 * @param selected
	 *            field "selected" value.
	 */
	public void setFieldSelected(FIELD filed, boolean selected) {
		this.selected.put(filed, selected);
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
		mapTo.put(field, target);
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
		this.selected.put(field, selected);
		mapTo.put(field, target);
	}

	/**
	 * Checks presence of a mapping for a specified key.
	 * 
	 * @param field
	 *            field to use.
	 * @return mapped field.
	 */
	public boolean haveMappingFor(FIELD field) {
		return selected.containsKey(field) || mapTo.containsKey(field);
	}

	/**
	 * Deletes a mapping for a field.
	 * 
	 * @param field
	 *            field to delete a mapping for.
	 */
	public void deleteMappingFor(FIELD field) {
		selected.remove(field);
		mapTo.remove(field);
	}

    /**
     * Adds a field if it does not exists. Field is unselected and have not
     * "map to" value.
     * 
     * @param field
     *            field to add.
     * @deprecated Functionality for this method is strange. It should be
     *             removed in later revisions. Use set* methods.
     */
	@Deprecated
	public void addField(FIELD field) {
		if (haveMappingFor(field))
			return;
		selected.put(field, true);
	}

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mapTo == null) ? 0 : mapTo.hashCode());
		result = prime * result
				+ ((selected == null) ? 0 : selected.hashCode());
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
		if (mapTo == null) {
			if (other.mapTo != null)
				return false;
		} else if (!mapTo.equals(other.mapTo))
			return false;
		if (selected == null) {
			if (other.selected != null)
				return false;
		} else if (!selected.equals(other.selected))
			return false;
		return true;
	}
}
