package com.ignfab.minalac.generator.modules.minecraft;

import java.util.HashMap;
import java.util.Map;

import io.github.ensgijs.nbt.tag.CompoundTag;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.TestingVoxelTile;

import static org.junit.jupiter.api.Assertions.*;

public class MinecraftVoxelTest {
    @Test
    public void testConstructor() {
        MinecraftVoxel air = new MinecraftVoxel("air");
        assertEquals("minecraft:air", air.type());
        assertNotNull(air.properties());

        Map<String, String> properties = new HashMap<>();
        properties.put("persistent", "true");
        MinecraftVoxel oakLeaves = new MinecraftVoxel("oak_leaves", properties);
        assertEquals(properties, oakLeaves.properties());
        assertNotSame(properties, oakLeaves.properties());
    }

    @Test
    public void testToString() {
        assertEquals("minecraft:air", new MinecraftVoxel("minecraft:air").toString());
        assertEquals("minecraft:blue_candle[candles=3,lit=true]", new MinecraftVoxel("minecraft:blue_candle", Map.of(
            "candles", "3",
            "lit", "true"
        )).toString());
    }

    @Test
    public void testFromString() {
        assertEquals(new MinecraftVoxel("minecraft:air"), MinecraftVoxel.fromString("minecraft:air"));
        assertEquals(new MinecraftVoxel("minecraft:air"), MinecraftVoxel.fromString("minecraft:air[]"));
        assertEquals(new MinecraftVoxel("minecraft:blue_candle", Map.of(
            "candles", "3",
            "lit", "true"
        )), MinecraftVoxel.fromString("minecraft:blue_candle[candles=3,lit=true]"));

        assertThrows(IllegalArgumentException.class, () -> MinecraftVoxel.fromString("minecraft:unclosed_bracket["));
        assertThrows(IllegalArgumentException.class, () -> MinecraftVoxel.fromString("minecraft:extra[]data"));
        assertThrows(IllegalArgumentException.class, () -> MinecraftVoxel.fromString("minecraft:invalid[property]"));
    }

    @Test
    public void testFromBlockState() {
        CompoundTag airState = new CompoundTag();
        airState.putString("Name", "minecraft:air");
        assertEquals(new MinecraftVoxel("minecraft:air"), MinecraftVoxel.fromBlockState(airState));
        CompoundTag candleState = new CompoundTag();
        candleState.putString("Name", "minecraft:blue_candle");
        CompoundTag properties = new CompoundTag();
        properties.putString("candles", "3");
        properties.putString("lit", "true");
        candleState.put("Properties", properties);
        assertEquals(new MinecraftVoxel("minecraft:blue_candle", Map.of(
            "candles", "3",
            "lit", "true"
        )), MinecraftVoxel.fromBlockState(candleState));
    }

    @Test
    public void testPlaceSimple() {
        MinecraftVoxelTileMock tile = new MinecraftVoxelTileMock(new WorldBBox3d(
            new WorldCoords3d(-50, -10, 0),
            new WorldCoords3d(10, 0, 200)
        ));

        MinecraftVoxel air = new MinecraftVoxel("minecraft:air");
        air.place(tile, 3, -7, 64);

        CompoundTag expectedAir = new CompoundTag();
        expectedAir.putString("Name", "minecraft:air");
        tile.assertBlockStateAt(3, 64, 6, expectedAir); // X/Y/Z => X/Z/-Y
    }

    @Test
    public void testPlaceWithProperties() {
        MinecraftVoxelTileMock tile = new MinecraftVoxelTileMock(new WorldBBox3d(
            new WorldCoords3d(-50, -10, 0),
            new WorldCoords3d(10, 0, 200)
        ));

        MinecraftVoxel stairs = new MinecraftVoxel("minecraft:oak_stairs", Map.of(
            "facing", "north",
            "half", "bottom",
            "shape", "straight",
            "waterlogged", "false"
        ));
        stairs.place(tile, -43, 0, 192);

        CompoundTag expectedStairs = new CompoundTag();
        expectedStairs.putString("Name", "minecraft:oak_stairs");
        CompoundTag properties = new CompoundTag();
        properties.putString("facing", "north");
        properties.putString("half", "bottom");
        properties.putString("shape", "straight");
        properties.putString("waterlogged", "false");
        expectedStairs.put("Properties", properties);
        tile.assertBlockStateAt(-43, 192, -1, expectedStairs); // X/Y/Z => X/Z/-Y
    }

    @Test
    public void testPlaceIllegal() {
        MinecraftVoxel air = new MinecraftVoxel("minecraft:air");
        assertThrows(IllegalArgumentException.class, () -> air.place(new TestingVoxelTile(WorldBBox3d.EMPTY), 0, 0, 0));
    }
}
