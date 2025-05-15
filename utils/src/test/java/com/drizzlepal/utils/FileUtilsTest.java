package com.drizzlepal.utils;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

public class FileUtilsTest {
    @Test
    public void testGetClassPathOutputStream() throws IOException {
        File classPathFile = FileUtils.getClassPathFile("test.txt");
        String absolutePath = classPathFile.getAbsolutePath();
        System.out.println(absolutePath);
    }

}
