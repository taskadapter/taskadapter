package com.taskadapter.license;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileDeleter {
    /**
     * Unfortunately, Google Guava deprecated and removed "Files.deleteDirectoryContents()" method in Guava 10+,
     * so we have to implement this ourselves.
     */
    public static void deleteRecursively(File file) throws IOException {
        if (file.isDirectory()) {
            for (File c : file.listFiles()) {
                deleteRecursively(c);
            }
        }
        if (!file.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + file);
        }
    }
}
