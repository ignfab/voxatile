package com.ignfab.minalac.generator.utils.modules;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;

/**
 * A module loader that can load modules and create a module manager out of them.
 */
public class ModulesLoader {

    private final List<ModuleCreator> creators = new LinkedList<>();

    /**
     * Adds a {@link ModuleCreator} to the loader.
     *
     * @param creator the module creator (could be an already instantiated {@link Module})
     */
    public void add(ModuleCreator creator) {
        creators.add(creator);
    }

    /**
     * Loads all module jars from a directory.
     * <p>
     * This method only loads jars in the given directory. Sub directories are not recursively loaded.
     *
     * @param directory Path of the directory to load
     *
     * @throws GenerationFailedException if there was a problem loading any of the jar files.
     */
    public void loadModulesDirectory(File directory) throws GenerationFailedException {
        File[] files = directory.listFiles();
        if (files == null)
            throw new GenerationFailedException("Could not list files in %d".formatted(directory.getPath()));

        for (File file : files) {
            if (file.isDirectory())
                continue;
            if (!file.getName().toLowerCase().endsWith(".jar"))
                continue;

            add(new JarModule(file));
        }
    }

    /**
     * Creates all modules out of module creators and put them in a module manager.
     *
     * @return the module manager containing created modules
     *
     * @throws GenerationFailedException if there was a problem creating any of the modules.
     */
    public ModulesManager create() throws GenerationFailedException {
        List<Module> modules = new ArrayList<>(creators.size());
        for (ModuleCreator creator : creators)
            modules.add(creator.create());

        return new ModulesManager(modules);
    }

}
