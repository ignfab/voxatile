package com.ignfab.minalac.generator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.parameters.ParamsParser;

public class Plugins {

    static final String PLUGIN_PROPERTIES = "plugin.properties";

    private final Map<String, Plugin> plugins = new HashMap<>();

    public Plugins() {
    }

    public void loadFromDirectory(Path path) throws GenerationFailedException {
        for (File file : path.toFile().listFiles()) {
            if (file.isDirectory())
                continue;
            String name = file.getName();
            if (!name.toLowerCase().endsWith(".jar"))
                continue;
            if (plugins.containsKey(name))
                continue;

            System.out.println("Loading plugin " + name);
            Plugin plugin = load(file);
            plugins.put(name, plugin);
            plugin.init();
        }
    }

    private Plugin load(File pluginJar) throws GenerationFailedException {

        try (
            // There may be cleaner ways to load classes:
            // https://stackoverflow.com/questions/60764/how-to-load-jar-files-dynamically-at-runtime
            URLClassLoader loader = new URLClassLoader(new URL[] { pluginJar.toURI().toURL() });
        ) {
            // Read manifest file
            InputStream is = loader.getResourceAsStream(PLUGIN_PROPERTIES);
            if (is == null)
                throw new GenerationFailedException(
                    "%s: could not load plugin: missing %s file in jar".formatted(pluginJar.getAbsolutePath(), PLUGIN_PROPERTIES));

            Properties properties = new Properties();
            properties.load(is);

            if (!properties.containsKey("class"))
                throw new GenerationFailedException(
                    "%s: could not load plugin: missing class property in %s file".formatted(pluginJar.getAbsolutePath(), PLUGIN_PROPERTIES));

            // Load plugin class
            String className = properties.getProperty("class");
            Class<?> pluginClass = loader.loadClass(className);
            if (!Plugin.class.isAssignableFrom(pluginClass))
                throw new GenerationFailedException(
                    "%s: could not load plugin: %s class does not extend Plugin class".formatted(pluginJar.getAbsolutePath(), className));

            // Instanciate plugin object
            return (Plugin)pluginClass.getDeclaredConstructor().newInstance();

        } catch (IOException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException | InstantiationException e) {
            throw new GenerationFailedException(
                "%s: could not load plugin".formatted(pluginJar.getAbsolutePath()), e);
        }
    }

    private void forEachPlugin(Consumer<Plugin> fct) {
        plugins.values().forEach(fct);
    }

    public void registerParams(ParamsParser parser) {
        forEachPlugin((Plugin plugin) -> { plugin.registerParams(parser); });
    }
}
