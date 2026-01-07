package com.ignfab.minalac.generator.utils.modules;

import java.util.List;

import com.ignfab.minalac.generator.parameters.ParamsParser;

/**
 * A manager for modules.
 * <p>
 * This class is basically a list of modules that triggers modules hooks.
 */
public class ModulesManager {

    private final List<Module> modules;

    /**
     * Creates a new modules manager.
     *
     * @param modules list of modules to include into modules manager.
     */
    public ModulesManager(List<Module> modules) {
        this.modules = modules;
    }

    /**
     * Registers parameters for all modules.
     * <p>
     * This calls {@link Module#registerParams(ParamsParser)} for each module with the given parser.
     *
     * @param parser parser into register modules params.
     */
    public void registerParams(ParamsParser parser) {
        modules.forEach(module -> module.registerParams(parser));
    }
}
