package com.ignfab.minalac.generator.utils.modules;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;

/**
 * Representation of a module jar file.
 */
public class JarModule implements ModuleCreator {
    /**
     * Name of module properties file in module jar files.
     * <p>
     * This file should include a {@code class} property that holds name of the {@link Module} subclass to instantiate in the module.
     */
    static final String MODULE_PROPERTIES = "module.properties";

    private final URL url;
    private final File file;
    private final Properties properties;

    /**
     * Creates a new JarModule object.
     *
     * @param jarFile Jar file to use for creation
     */
    public JarModule(File jarFile) throws GenerationFailedException {
        file = jarFile;
        properties = new Properties();
        try {
            url = file.toURI().toURL();

            InputStream is = new URL("jar:" + url + "!/" + MODULE_PROPERTIES).openStream();
            if (is == null)
                throw new IOException(
                    "Missing %s file in jar".formatted(MODULE_PROPERTIES));

            properties.load(is);

            if (!properties.containsKey("class"))
                throw new IOException(
                    "Missing class property in %s file".formatted(MODULE_PROPERTIES));

        } catch (IOException e) {
            throw new GenerationFailedException(
                "%s: could not load module".formatted(file.getAbsolutePath()), e);
        }
    }

    @Override
    public Module create() throws GenerationFailedException {
        try {
            URLClassLoader loader = new URLClassLoader(new URL[] { url });

            String className = properties.getProperty("class");

            Class<?> moduleClass = Class.forName(className, true, loader);

            // Instantiate module object
            return moduleClass.asSubclass(Module.class).getDeclaredConstructor().newInstance();

        } catch (ReflectiveOperationException | ClassCastException e) {
            throw new GenerationFailedException(
                "%s: could not load module".formatted(file.getAbsolutePath()), e);
        }
    }
}
