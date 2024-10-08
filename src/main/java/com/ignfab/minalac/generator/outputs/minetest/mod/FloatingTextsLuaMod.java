package com.ignfab.minalac.generator.outputs.minetest.mod;

import com.ignfab.minalac.generator.outputs.minetest.MTVoxelWorld;
import com.ignfab.minalac.generator.world.MapWriteException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FloatingTextsLuaMod implements LuaMod {
    private final List<FloatingText> texts = new ArrayList<>();

    // In-Game coords
    public void addText(double x, double y, double z, String text) {
        texts.add(new FloatingText(x, y, z, text));
    }

    @Override
    public void save(File directory) throws MapWriteException {
        if (texts.isEmpty())
            return;

        StringBuilder code = new StringBuilder("minetest.register_on_joinplayer(function(p)\n");
        for (FloatingText text : texts) {
            code.append("""
                p:hud_add({
                  hud_elem_type = "waypoint",
                  name = "%s",
                  precision = 0,
                  number = 0xFFFFFF,
                  world_pos = { x = %f, y = %f, z = %f }
                })
                """.formatted(text.escaped(), text.x(), text.z(), text.y()));
        }
        code.append("end)");
        MTVoxelWorld.createFile(new File(directory, "init.lua"), code.toString());
    }

    private record FloatingText(double x, double y, double z, String text) {
        public String escaped() {
            return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
        }
    }
}
