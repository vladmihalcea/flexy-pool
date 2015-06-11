package com.vladmihalcea.flexypool.util;

import com.vladmihalcea.flexypool.config.PropertyLoader;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * PropertiesTestUtils - Properties Test Utils
 *
 * @author Vlad Mihalcea
 */
public class PropertiesTestUtils {

    private static File propertiesFile = new File(
            ClassLoaderUtils.getClassLoader().getResource(".").getFile(),
            PropertyLoader.PROPERTIES_FILE_NAME);

    public static void init() {
        try {
            propertiesFile.delete();
            propertiesFile.createNewFile();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Properties getProperties(PropertyLoader propertyLoader) {
        return (Properties) ReflectionTestUtils.getField(propertyLoader, "properties");
    }

    public static void setProperties(Properties properties) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(propertiesFile);
            properties.store(outputStream, "");
        } finally {
            if(outputStream != null) {
                outputStream.close();
            }
        }
    }
}
