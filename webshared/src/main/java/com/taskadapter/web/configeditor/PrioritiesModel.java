package com.taskadapter.web.configeditor;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.taskadapter.connector.Priorities;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;

final class PrioritiesModel implements Container,
        Container.ItemSetChangeNotifier, Container.PropertySetChangeNotifier {
    private static final long serialVersionUID = 1L;

    private static final Map<String, Class<?>> PROPTYPES = new HashMap<>();

    static {
        PROPTYPES.put("text", String.class);
        PROPTYPES.put("value", Integer.class);
    }

    /**
     * Used priorities.
     */
    private final Priorities priorities;

    /**
     * Item mapping.
     */
    private final Map<String, Item> items = new LinkedHashMap<>();

    private final List<ItemSetChangeListener> itemListeners = new LinkedList<>();

    PrioritiesModel(Priorities priorities) {
        this.priorities = priorities;
        fillItems(priorities);
    }

    @Override
    public void addPropertySetChangeListener(PropertySetChangeListener listener) {
        // not used
    }

    @Deprecated
    @Override
    public void addListener(PropertySetChangeListener listener) {
        // not used
    }

    @Override
    public void removePropertySetChangeListener(PropertySetChangeListener listener) {
        // not used
    }

    @Deprecated
    @Override
    public void removeListener(PropertySetChangeListener listener) {
        // not used
    }

    @Override
    public void addItemSetChangeListener(ItemSetChangeListener listener) {
        itemListeners.add(listener);
    }

    @Deprecated
    @Override
    public void addListener(ItemSetChangeListener listener) {
        itemListeners.add(listener);
    }

    @Override
    public void removeItemSetChangeListener(ItemSetChangeListener listener) {
        itemListeners.remove(listener);
    }

    @Deprecated
    @Override
    public void removeListener(ItemSetChangeListener listener) {
        itemListeners.remove(listener);
    }

    @Override
    public Item getItem(Object itemId) {
        return items.get(itemId);
    }

    @Override
    public Collection<?> getContainerPropertyIds() {
        return PROPTYPES.keySet();
    }

    @Override
    public Collection<?> getItemIds() {
        return items.keySet();
    }

    @Override
    public Property getContainerProperty(Object itemId, Object propertyId) {
        final Item item = getItem(itemId);
        if (item == null)
            return null;
        return item.getItemProperty(propertyId);
    }

    @Override
    public Class<?> getType(Object propertyId) {
        return PROPTYPES.get(propertyId);
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean containsId(Object itemId) {
        return items.containsKey(itemId);
    }

    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object addItem() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeItem(Object itemId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type,
            Object defaultValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeContainerProperty(Object propertyId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Updates a content from a map.
     *
     * @param props
     *            used properties.
     */
    void updateContent(Priorities props) {
        this.priorities.clear();
        for (String key : props.getAllNames())
            priorities.setPriority(key, props.getPriorityByText(key));
        fillItems(props);
    }

    /**
     * Fills a property set.
     *
     * @param props priorities.
     */
    void fillItems(Priorities props) {
        items.clear();
        for (String key : props.getAllNames())
            items.put(key, createItem(key));
        final ItemSetChangeEvent evt = new ItemSetChangeEvent() {
            private static final long serialVersionUID = 1L;

            @Override
            public Container getContainer() {
                return PrioritiesModel.this;
            }
        };
        for (ItemSetChangeListener iscl : itemListeners
                .toArray(new ItemSetChangeListener[itemListeners.size()]))
            iscl.containerItemSetChange(evt);
    }

    /**
     * Creates a new item.
     *
     * @param key
     *            item key.
     * @return created item.
     */
    private Item createItem(String key) {
        final ObjectProperty<String> text = new ObjectProperty<>(key);
        text.setReadOnly(true);

        final Property value = new PriorityValue(priorities, key);

        final Map<String, Property> propmap = new HashMap<>();
        propmap.put("text", text);
        propmap.put("value", value);

        return ConstItem.constItem(propmap);
    }

    /**
     * Checks model for valid and invalid "mapped" values. Returns priority
     * names and user input values for "invalid" fields. Such invalid values are
     * not set to underlying model and value in model is undefiend. When all
     * data is valid returns an empty map.
     *
     * @return mapping from keys to invalid user-input values.
     */
    public Map<String, String> getInvalidValues() {
        final Map<String, String> result = new HashMap<>();
        for (Item item : items.values()) {
            final PriorityValue userValue = (PriorityValue) item
                    .getItemProperty("value");
            if (!userValue.isValid()) {
                result.put((String) item.getItemProperty("text").getValue(),
                        (String) userValue.getValue());
            }
        }
        return result;
    }
}
