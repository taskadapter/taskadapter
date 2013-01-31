package com.taskadapter.web.configeditor.file;

import java.io.File;

/**
 * File utilities.
 * 
 */
final class FileUtils {
    /**
     * Returns a filename for a given path. Returned result never contains any
     * path component.
     * 
     * @param path
     *            path to get a file name from.
     * @return file name (not a path) component of a path.
     */
    public static String basename(String path) {
        return new File(path).getName();
    }
}
