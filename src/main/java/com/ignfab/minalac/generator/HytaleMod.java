package com.ignfab.minalac.generator;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.events.AddWorldEvent;

import com.ignfab.minalac.generator.outputs.hytale.HytaleVoxelWorld;

public class HytaleMod extends JavaPlugin {
    public HytaleMod(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        this.getEventRegistry().registerGlobal(AddWorldEvent.class, event -> {
            HytaleVoxelWorld.world = event.getWorld();
            new Thread(() -> {
                try {
                    MinalacGenerator.main(new String[]{ "-p", "../examples/hytale.yaml", "unused" });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, "MinalacGeneratorThread").start();
        });
    }
}
