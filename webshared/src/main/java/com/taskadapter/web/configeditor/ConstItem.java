package com.taskadapter.web.configeditor;

import java.util.Collection;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * "Constant" item with properties. Have constant properties set.
 * 
 * @author maxkar
 * 
 */
public final class ConstItem implements Item, Item.PropertySetChangeNotifier {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Properties of the item.
	 */
	private final Map<?, Property> properties;

	/**
	 * Creates a new constant item.
	 * 
	 * @param properties
	 *            item properties.
	 */
	private ConstItem(Map<? extends Object, Property> properties) {
		this.properties = properties;
	}

	@Override
	public Property getItemProperty(Object id) {
		return properties.get(id);
	}

	@Override
	public Collection<?> getItemPropertyIds() {
		return properties.entrySet();
	}

	@Override
	public boolean addItemProperty(Object id, Property property)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeItemProperty(Object id)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addListener(PropertySetChangeListener listener) {
		// not needed
	}

	@Override
	public void removeListener(PropertySetChangeListener listener) {
		// not needed
	}

	/**
	 * Creates a new object with a fixed set of properties.
	 * 
	 * @param properties
	 *            properties set.
	 * @return item with a fixed properties.
	 */
	public static Item constItem(Map<? extends Object, Property> properties) {
		return new ConstItem(properties);
	}
}
