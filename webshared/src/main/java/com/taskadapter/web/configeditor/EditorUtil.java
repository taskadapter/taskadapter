package com.taskadapter.web.configeditor;

import com.taskadapter.model.NamedKeyedObject;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alexey Skorokhodov
 */
public class EditorUtil {
    public static final int NARROW_WIDTH = 150;

    public static void setNullSafe(AbstractTextField field, Object value) {
        if (value != null) {
            field.setValue(value.toString());
        }
    }

    public static String showList(Object[] objects) {
        System.out.println("SHOW list here");
        return null;
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

    public static Button createLookupButton(final Window window, String label, String description, final LookupOperation operation, final TextField destinationForKey, final boolean useValue) {
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
                Map<String, String> map = new HashMap<String, String>();
                for (NamedKeyedObject o : objects) {
                    map.put(o.getName(), o.getKey());
                }

                String selectedName = showList(map.keySet().toArray());
                if (selectedName != null) {
                    if (useValue) {
                        destinationForKey.setValue(selectedName);
                    } else {
                        String key = map.get(selectedName);
                        destinationForKey.setValue(key);
                    }
                }
            }
        };
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                LookupJob job = new LookupJob(window, operation, listener);
                job.start();
            }
        });
        return button;
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
