package com.taskadapter.web.configeditor.map;

import java.util.HashMap;
import java.util.Map;

import com.taskadapter.web.configeditor.ConstItem;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * Item holder.
 * 
 * @author maxkar
 * 
 */
final class ItemHolder {
	/**
	 * Item id.
	 */
	final int id;

	/**
	 * Bound item.
	 */
	final Item item;

	/**
	 * Key.
	 */
	final KeyProperty key;

	/**
	 * Value.
	 */
	final ValueProperty value;

	ItemHolder(int id, KeyProperty key, ValueProperty value) {
		this.id = id;
		this.key = key;
		this.value = value;

		final Map<String, Property> content = new HashMap<String, Property>();
		content.put("id", key);
		content.put("value", value);
		this.item = ConstItem.constItem(content);
	}

}
