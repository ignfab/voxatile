package com.ignfab.minalac.generator;

import com.ignfab.minalac.generator.parameters.ParamsParser;

/**
 * Main plugin class parent.
 *
 * All plugins should derivate a class from this one and eventually override its methods.
 * That class will be the entrypoint of the plugin.
 *
 * If the plugin is packaged as a separate jar file, this jar file should include a {@code plugin.properties}
 * file with a {@code class} field containing the canonical name of plugin derivated class.
 */
public abstract class Plugin {

    /**
     * Creates a new plugin object.
     * To instanciate plugins, {@link PluginsManager} will always use this constructor with no arguments.
     */
    public Plugin() {}

    /**
     * Initializes plugin.
     * This will be called as soon as the plugin is added to {@link PluginsManager}.
     */
    public void init() {}

    /**
     * Registers parameters for the plugin.
     * This will be called before generator reads parameters. This is the place to register new parameter classes into parser.
     *
     * @param parser Parameter parser to use
     */
    public void registerParams(ParamsParser parser) {}
}
