package com.taskadapter.connector.definition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    // TODO REVIEW Have you considered storing this in one map completely describing one field?
    // It could make much more sense. We just extract descriptor for one field and do everything we want. And we could put some "converter" logic there, etc...
    // UPDATED: Have you considered storing this in one map completely describing one field? It could make much more sense. We just extract desciptor for one field and do everything we want. And we could put some "converter" logic there, etc...
	private final Map<FIELD, String> defaultValuesForEmptyFields;

	/**
	 * Creates empty mappings.
	 */
	public Mappings() {
		this.selected = new HashMap<>();
		this.mapTo = new HashMap<>();
		defaultValuesForEmptyFields = new HashMap<>();
	}

	/**
	 * Copy constructor for mapping.
	 * 
	 * @param mapping
	 *            new mapping.
	 */
	public Mappings(Mappings mapping) {
		this.selected = new HashMap<>(mapping.selected);
		this.mapTo = new HashMap<>(mapping.mapTo);
        // TODO REVIEW Why not EnumMap?
		defaultValuesForEmptyFields = new HashMap<>(mapping.defaultValuesForEmptyFields);
	}

	public Map<String, String> getDefaultValuesForEmptyFields() {
		/* cannot use Collectors.toMap here because it does not support null values. boo Java!
		 see https://stackoverflow.com/questions/42546950/use-java-8-streams-to-transform-a-map-with-nulls

			defaultValuesForEmptyFields.entrySet().stream().collect(
				Collectors.toMap(k-> k.getKey().name(), Map.Entry::getValue));
		*/
		Map<String, String> copy = new HashMap<>();
		for (Map.Entry<FIELD, String> entry : defaultValuesForEmptyFields.entrySet()) {
			copy.put(entry.getKey() != null ? entry.getKey().name() : null,
					entry.getValue()
			);
		}
		return copy;
	}

	/**
	 * Checks if field is selected for conversion.
	 * 
	 * @return <code>true</code> iff field is selected for conversion.
	 */
	public boolean isFieldSelected(FIELD field) {
		final Boolean result = selected.get(field);
		return result != null && result;
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
	public void setMapping(FIELD field, boolean selected, String target, String defaultValueForEmptyField) {
		this.selected.put(field, selected);
		mapTo.put(field, target);
		defaultValuesForEmptyFields.put(field, defaultValueForEmptyField);
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

	public String getDefaultValueForEmptyField(FIELD field) {
		return defaultValuesForEmptyFields.get(field);
	}

    @Override
	public int hashCode() {
        // TODO REVIEW Why mappings with different default values are considered equal? Could we safely replace
        // one mapping instance with another? Why do we have equals/hashCode for this class?
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

	public Collection<FIELD> getSelectedFields() {
		return selected.keySet().stream().filter(this::isFieldSelected).collect(Collectors.toSet());
	}
}
