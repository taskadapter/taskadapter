package com.taskadapter.connector.common;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class FileNameGeneratorTest {
    @Test
    public void secondFileNameIsDifferentFromFirstWhenFileAlreadyExists() throws IOException {
        var folder = new File(System.getProperty("java.io.tmpdir"));

        var filePattern = "file_%d.txt";
        var file = FileNameGenerator.findSafeAvailableFileName(folder, filePattern);
        file.createNewFile();
        file.deleteOnExit();

        var file2 = FileNameGenerator.findSafeAvailableFileName(folder, filePattern);
        assertThat(file2.getName()).isNotEqualTo(file.getName());
        file2.deleteOnExit();
    }
}
