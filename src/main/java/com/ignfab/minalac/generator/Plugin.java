package com.ignfab.minalac.generator;

import com.ignfab.minalac.generator.parameters.ParamsParser;

public abstract class Plugin {

    public Plugin() {}

    public void init() {}

    public void registerParams(ParamsParser parser) {}
}
