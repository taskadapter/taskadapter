package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.ExceptionFormatter;
import com.taskadapter.web.callbacks.DataProvider;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EditorUtil {

    private static void showList(String windowTitle, String listTitle, Collection<String> items, ValueListener valueListener) {
        ListSelectionDialog newWindow = new ListSelectionDialog(windowTitle, listTitle, items, valueListener);
        newWindow.center();
        newWindow.setModal(true);

        UI.getCurrent().addWindow(newWindow);
        newWindow.focus();
    }

    public static void show(String caption, Exception e) {
        String errorMessage = getRoot(e).getMessage();
        Notification.show(caption, errorMessage, Notification.Type.ERROR_MESSAGE);
    }

    // TODO can't move this to ButtonBuilder class right now because it's not accessible from webshared module.
    public static Button createButton(String label, String description, Button.ClickListener clickListener) {
        Button button = new Button(label);
        button.setDescription(description);
        button.addClickListener(clickListener);
        return button;
    }
    
    public static TextField textInput(Property property, String width) {
        final TextField result = new TextField();
        result.setPropertyDataSource(property);
        result.setWidth(width);
        return result;
    }
    
    public static TextField textInput(Property property) {
        final TextField result = new TextField();
        result.setPropertyDataSource(property);
        return result;
    }
    
    public static TextField propertyInput(Object o, String field) {
        return textInput(new MethodProperty<Object>(o, field));
    }

    // TODO review and refactor this. this method is too complex
	public static Button createLookupButton(
			final String buttonLabel,
			String description, final String windowTitle,
			final String listTitle,
			final DataProvider<List<? extends NamedKeyedObject>> operation,
			final Property destination, final boolean useValue, 
			final ExceptionFormatter errorFormatter) {
        Button button = new Button(buttonLabel);
        button.setDescription(description);
        final LookupResultListener listener = new LookupResultListener() {
            @Override
            public void notifyDone(List<? extends NamedKeyedObject> objects) {
                if (!objects.isEmpty()) {
                    showValues(destination, useValue, objects);
                }
            }

            private void showValues(final Property destination, final boolean useValue,
                                    List<? extends NamedKeyedObject> objects) {
                final Map<String, String> map = new TreeMap<String, String>();
                for (NamedKeyedObject o : objects) {
                    map.put(o.getName(), o.getKey());
                }

                showList(windowTitle, listTitle, map.keySet(), new ValueListener() {
                    @Override
                    public void setValue(String value) {
                        if (useValue) {
                            destination.setValue(value);
                        } else {
                            String key = map.get(value);
                            destination.setValue(key);
                        }

                    }
                });
            }
        };
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                final List<? extends NamedKeyedObject> objects;
                try {
                    objects = operation.loadData();

                    if (objects.isEmpty()) {
                        Notification.show("No objects", "No objects have been found", Notification.Type.HUMANIZED_MESSAGE);
                    }
                    listener.notifyDone(objects);
                } catch (BadConfigException e) {
                    Notification.show("", errorFormatter.formatError(e), Notification.Type.HUMANIZED_MESSAGE);
                } catch (Exception e) {
                    EditorUtil.show("Something went wrong", e);
                }
            }
        });
        return button;
    }
	
    public interface ValueListener {
        void setValue(String value);
    }

    public static TextField addLabeledText(AbstractLayout layout, String caption, String tooltip) {
        Label label = new Label(caption);
        layout.addComponent(label);

        TextField field = new TextField();
        field.setDescription(tooltip);
        layout.addComponent(field);

        if (layout instanceof Layout.AlignmentHandler) {
            ((Layout.AlignmentHandler) layout).setComponentAlignment(label, Alignment.MIDDLE_LEFT);
            ((Layout.AlignmentHandler) layout).setComponentAlignment(field, Alignment.MIDDLE_CENTER);
        }

        return field;
    }

    public static Throwable getRoot(Throwable t) {
        Throwable result = t;
        while (result.getCause() != null) {
            result = result.getCause();
        }
        return result;
    }

    /**
     * Wraps a nulls to an empty string.
     * @param property property to wrap.
     * @return wrapped property.
     */
    public static Property wrapNulls(final AbstractProperty property) {
    	return new AbstractProperty() {
			private static final long serialVersionUID = 1L;

			@Override
			public void setValue(Object newValue) throws ReadOnlyException,
                    Converter.ConversionException {
				if (newValue instanceof String && ((String) newValue).isEmpty())
					property.setValue(null);
				else
					property.setValue(newValue);
				fireValueChange();
			}
			
			@Override
			public void setReadOnly(boolean newStatus) {
				property.setReadOnly(newStatus);
				fireReadOnlyStatusChange();
			}
			
			@Override
			public boolean isReadOnly() {
				return property.isReadOnly();
			}
			
			@Override
			public Object getValue() {
				final Object v = property.getValue();
                return v == null ? "" : v.toString();
			}
			
			@Override
			public Class<?> getType() {
				return String.class;
			}

			@Override
			public String toString() {
				final Object value = property.getValue();
				return value == null ? "" : value.toString();
			}
		};
    	
    }
}
