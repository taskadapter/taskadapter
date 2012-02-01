package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.WindowProvider;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alexey Skorokhodov
 */
public class EditorUtil {

    public static void setNullSafe(AbstractTextField field, Object value) {
        if (value != null) {
            field.setValue(value.toString());
        }
    }

    public static void showList(WindowProvider windowProvider, String title, Collection<String> items, ValueListener valueListener) {
        ListSelectionDialog newWindow = new ListSelectionDialog(title, items, valueListener);
        windowProvider.getWindow().addWindow(newWindow);
    }

    public static void show(Window window, String caption, Exception e) {
        String errorMessage = getRoot(e).getMessage();
        window.showNotification(caption, errorMessage);
    }

    public static void show(Window window, String caption, String message) {
        window.showNotification(caption, message);
    }

    public static Button createButton(String label, String description, Button.ClickListener clickListener) {
        Button button = new Button(label);
        button.setDescription(description);
        button.addListener(clickListener);
        return button;
    }

    public static Button createLookupButton(final WindowProvider windowProvider, final String label, String description, final LookupOperation operation, final TextField destinationForKey, final boolean useValue) {
        Button button = new Button(label);
        button.setDescription(description);
        final LookupResultListener listener = new LookupResultListener() {
            @Override
            public void notifyDone(List<? extends NamedKeyedObject> objects) {
                if (!objects.isEmpty()) {
                    showValues(destinationForKey, useValue, objects);
                }
            }

            private void showValues(final TextField destinationForKey,
                                    final boolean useValue,
                                    List<? extends NamedKeyedObject> objects) {
                final Map<String, String> map = new HashMap<String, String>();
                for (NamedKeyedObject o : objects) {
                    map.put(o.getName(), o.getKey());
                }

                showList(windowProvider, label, map.keySet(), new ValueListener() {
                    @Override
                    public void setValue(String value) {
                        if (useValue) {
                            destinationForKey.setValue(value);
                        } else {
                            String key = map.get(value);
                            destinationForKey.setValue(key);
                        }

                    }
                });
            }
        };
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                final List<? extends NamedKeyedObject> objects;
                try {
                    objects = operation.run();

                    if (objects.isEmpty()) {
                        windowProvider.getWindow().showNotification("No objects", "No objects have been found");
                    }
                    listener.notifyDone(objects);
                } catch (ValidationException e) {
                    EditorUtil.show(windowProvider.getWindow(), "Validation failed", e);
                } catch (Exception e) {
                    EditorUtil.show(windowProvider.getWindow(), "Operation failed", e);
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
        return field;
    }

    public static TextField createLabeledTextWidth(String label, String description, int width) {
        TextField field = new TextField(label);
        field.setDescription(description);
        field.setWidth(width, Sizeable.UNITS_PIXELS);
        return field;
    }

    public static Throwable getRoot(Throwable t) {
        Throwable result = t;
        while (result.getCause() != null) {
            result = result.getCause();
        }
        return result;
    }

}
