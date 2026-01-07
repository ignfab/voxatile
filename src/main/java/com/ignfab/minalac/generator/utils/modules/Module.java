package com.ignfab.minalac.generator.utils.modules;

import com.ignfab.minalac.generator.parameters.ParamsParser;

/**
 * Main module class parent.
 * <p>
 * All modules should subclass this one and eventually override its methods.
 * That class will be the entrypoint of the module.
 * It must have a public no-argument constructor that will be used by to instantiate it.
 * <p>
 * If the module is packaged as a separate jar file, this jar file should include a {@code module.properties}
 * file with a {@code class} field containing the canonical name of module subclass.
 */
public abstract class Module implements ModuleCreator {
    /**
     * Registers parameters for the module.
     * This will be called before generator reads parameters. This is the place to register new parameter classes into parser.
     *
     * @param parser Parameter parser to use
     */
    public void registerParams(ParamsParser parser) {}

    @Override
    public final Module create() {
        return this;
    }
}
