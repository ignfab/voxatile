package com.ignfab.minalac.generator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.parameters.ParamsParser;

/**
 * A manager for plugins.
 *
 * This class keeps a list of plugins which can be loaded from jar files or from directly added as a {@link Plugin} object.
 *
 * It can trigger plugin hooks.
 */
public class PluginsManager {

    /**
     * Name of plugin properties file in plugin jar files.
     *
     * This file should include a {@code class} property that holds name of the {@link Plugin} derivated class to instanciate in the plugin.
     */
    static final String PLUGIN_PROPERTIES = "plugin.properties";

    private final List<Plugin> plugins = new LinkedList<>();

    /**
     * Representation of a plugin jar file.
     *
     * @param properties Propeties read for plugin.properties file
     * @param file Jar file
     * @param url Jar file URL
     */
    private record PluginJar(Properties properties, File file, URL url) {

        public String className() {
            return properties.getProperty("class");
        }

        public String fileName() {
            return file.getName();
        }

        public String filePath() {
            return file.getAbsolutePath();
        }

        public static PluginJar fromFile(File file) throws GenerationFailedException {
            URL url;
            Properties properties = new Properties();
            try {
                // TODO: We could allow non plugin jars to be put in plugin directory.
                // in that case, they would be loaded but with no entry point. This would
                // allow to have a Jar used by serveral plugins.

                url = file.toURI().toURL();

                InputStream is = new URL("jar:" + url + "!/" + PLUGIN_PROPERTIES).openStream();
                if (is == null)
                    throw new GenerationFailedException(
                        "%s: could not load plugin: missing %s file in jar".formatted(file.getAbsolutePath(), PLUGIN_PROPERTIES));

                properties.load(is);

                if (!properties.containsKey("class"))
                        throw new GenerationFailedException(
                            "%s: could not load plugin: missing class property in %s file".formatted(file.getAbsolutePath(), PLUGIN_PROPERTIES));

            } catch (IOException e) {
                throw new GenerationFailedException(
                    "%s: could not load plugin".formatted(file.getAbsolutePath()), e);
            }

            return new PluginJar(properties, file, url);
        }
    }

    /**
     * Adds an internal (as object) plugin to managed plugins.
     *
     * @param plugin plugin to add.
     */
    public void add(Plugin plugin) {
        plugins.add(plugin);
        plugin.init();
    }

    /**
     * Loads all plugin jars from a directory.
     *
     * @param path Path of the directory to load
     *
     * @throws GenerationFailedException if there was a problem loading any of the jar files.
     */
    public void loadFromDirectory(Path path) throws GenerationFailedException {
        Map<String, PluginJar> jarsToAdd = new LinkedHashMap<>();

        for (File file : path.toFile().listFiles()) {
            if (file.isDirectory())
                continue;
            String name = file.getName();
            if (!name.toLowerCase().endsWith(".jar"))
                continue;

            jarsToAdd.put(name, PluginJar.fromFile(file));
        }

        if (jarsToAdd.isEmpty())
            return;

        URL[] urls = jarsToAdd.values().stream().map(PluginJar::url).toArray(size -> new URL[size]);

        Thread.currentThread().setContextClassLoader(
            new URLClassLoader(urls, Thread.currentThread().getContextClassLoader())
        );

        for (PluginJar jar : jarsToAdd.values()) {
            System.out.println("Loading plugin " + jar.fileName());

            try {
                // Load plugin class
                Class<?> pluginClass = Thread.currentThread().getContextClassLoader().loadClass(jar.className());
                if (!Plugin.class.isAssignableFrom(pluginClass))
                    throw new GenerationFailedException(
                        "%s: could not load plugin: %s class does not extend Plugin class".formatted(jar.filePath(), jar.className()));

                // Instanciate plugin object
                add((Plugin) pluginClass.getDeclaredConstructor().newInstance());

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                throw new GenerationFailedException(
                    "%s: could not load plugin".formatted(jar.filePath()), e);
            }
        }
    }

    /**
     * Registers parameters for all plugins.
     *
     * This calls {@link Plugin#registerParams(ParamsParser)} for each plugin with the given parser.
     *
     * @param parser parser into register plugins params.
     */
    public void registerParams(ParamsParser parser) {
        plugins.forEach((Plugin plugin) -> {
            plugin.registerParams(parser);
        });
    }
}
