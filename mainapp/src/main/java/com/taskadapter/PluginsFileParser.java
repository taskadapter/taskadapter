package com.taskadapter;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class PluginsFileParser {
    private static final String COMMENT_SYMBOL = "#";

    public Collection<String> parseResource(String resourceName) {
        try {
            String fileContents = Resources.toString(Resources.getResource(resourceName), Charsets.UTF_8);
            return parsePluginsFile(fileContents);
        } catch (IOException e) {
            throw new RuntimeException("can't load " + resourceName + " from classpath." + e.toString());
        }
    }

    public Collection<String> parsePluginsFile(String fileContents) {
        String[] strings = fileContents.split("\r\n|\r|\n");
        Collection<String> items = new ArrayList<String>();
        for (String s : strings) {
            if (s.startsWith(COMMENT_SYMBOL) || s.isEmpty()) {
                continue;
            }
            items.add(s);
        }
        return items;
    }

}
