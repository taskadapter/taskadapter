package com.taskadapter;

import com.taskadapter.util.MyIOUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Alexey Skorokhodov
 */
public class PluginsFileParser {
    private static final String COMMENT_SYMBOL = "#";

    public Collection<String> parseResource(String resourceName) {
        try {
            String fileContents = MyIOUtils.getResourceAsString(resourceName);
            return parsePluginsFile(fileContents);
        } catch (IOException e) {
            throw new RuntimeException("can't load " + resourceName + " from classpath." + e.toString());
        }
    }

    // TODO add unit test
    private Collection<String> parsePluginsFile(String fileContents) {
        String[] strings = fileContents.split("\r\n|\r|\n");
        Collection<String> items = new ArrayList<String>();
        for (String s : strings) {
            if (s.startsWith(COMMENT_SYMBOL)) {
                continue;
            }
            items.add(s);
        }
        return items;
    }

}
