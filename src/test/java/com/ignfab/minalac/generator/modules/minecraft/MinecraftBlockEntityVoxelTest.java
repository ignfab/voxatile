package com.ignfab.minalac.generator.modules.minecraft;

import java.util.Map;

import io.github.ensgijs.nbt.tag.CompoundTag;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.TestingVoxelTile;

import static org.junit.jupiter.api.Assertions.*;

public class MinecraftBlockEntityVoxelTest {
    @Test
    public void testConstructor() {
        MinecraftBlockEntityVoxel block = new MinecraftBlockEntityVoxel("type", "id", null, null);
        assertEquals("minecraft:type", block.type());
        assertEquals("minecraft:id", block.id());
        assertNotNull(block.properties());
        assertNotNull(block.data());

        CompoundTag data = new CompoundTag();
        data.putLong("OutputSignal", 7);
        MinecraftBlockEntityVoxel comparator = new MinecraftBlockEntityVoxel("comparator", "comparator", null, data);
        assertEquals(data, comparator.data());
        assertNotSame(data, comparator.data());

        MinecraftBlockEntityVoxel daylightDetector = new MinecraftBlockEntityVoxel(new MinecraftVoxel("minecraft:daylight_detector", Map.of("inverted", "true")), "minecraft:daylight_detector", null);
        assertEquals("minecraft:daylight_detector", daylightDetector.type());
        assertEquals(Map.of("inverted", "true"), daylightDetector.properties());
    }

    @Test
    public void testStripBlockEntity() {
        MinecraftBlockEntityVoxel beacon = new MinecraftBlockEntityVoxel("minecraft:beacon", "minecraft:beacon", null, null);
        assertEquals(new MinecraftVoxel("minecraft:beacon"), beacon.stripBlockEntity());

        CompoundTag data = new CompoundTag();
        data.putLong("OutputSignal", 7);
        MinecraftBlockEntityVoxel comparator = new MinecraftBlockEntityVoxel("comparator", "comparator", Map.of("powered", "true"), data);
        assertEquals(new MinecraftVoxel("minecraft:comparator", Map.of("powered", "true")), comparator.stripBlockEntity());
    }

    @Test
    public void testToString() {
        assertEquals("minecraft:beacon", new MinecraftBlockEntityVoxel("minecraft:beacon", "minecraft:beacon", null, null).toString());

        CompoundTag data = new CompoundTag();
        CompoundTag item = new CompoundTag();
        item.putString("id", "minecraft:music_disc_cat");
        data.put("RecordItem", item);
        data.putLong("ticks_since_song_started", 0);
        assertEquals(
            "minecraft:jukebox[has_record=true]{RecordItem:{id:\"minecraft:music_disc_cat\"},ticks_since_song_started:0l}",
            new MinecraftBlockEntityVoxel("minecraft:jukebox", "minecraft:jukebox", Map.of("has_record", "true"), data).toString()
        );
    }

    @Test
    public void testFromString() {
        MinecraftBlockEntityVoxel beacon = new MinecraftBlockEntityVoxel("minecraft:beacon", "minecraft:beacon", null, null);
        assertEquals(beacon, MinecraftBlockEntityVoxel.fromString("minecraft:beacon", "minecraft:beacon"));
        assertEquals(beacon, MinecraftBlockEntityVoxel.fromString("minecraft:beacon"));
        assertEquals(beacon, MinecraftBlockEntityVoxel.fromString("minecraft:beacon{}"));
        assertEquals(beacon, MinecraftBlockEntityVoxel.fromString("minecraft:beacon[]"));
        assertEquals(beacon, MinecraftBlockEntityVoxel.fromString("minecraft:beacon[]{}"));

        CompoundTag data = new CompoundTag();
        CompoundTag item = new CompoundTag();
        item.putString("id", "minecraft:music_disc_cat");
        data.put("RecordItem", item);
        data.putLong("ticks_since_song_started", 0);
        assertEquals(
            new MinecraftBlockEntityVoxel("minecraft:jukebox", "minecraft:jukebox", Map.of("has_record", "true"), data),
            MinecraftBlockEntityVoxel.fromString("minecraft:jukebox[has_record=true]{RecordItem:{id:\"minecraft:music_disc_cat\"},ticks_since_song_started:0l}")
        );

        assertThrows(IllegalArgumentException.class, () -> MinecraftBlockEntityVoxel.fromString("minecraft:unclosed_bracket{"));
        assertThrows(IllegalArgumentException.class, () -> MinecraftBlockEntityVoxel.fromString("minecraft:extra{}data"));
        assertThrows(IllegalArgumentException.class, () -> MinecraftBlockEntityVoxel.fromString("minecraft:invalid{nbt}"));
    }

    @Test
    public void testFromBlockEntity() {
        CompoundTag beaconEntity = new CompoundTag();
        beaconEntity.putString("id", "minecraft:beacon");
        beaconEntity.putBoolean("keepPacked", false);
        beaconEntity.putInt("x", 1);
        beaconEntity.putInt("y", 2);
        beaconEntity.putInt("z", 3);
        assertEquals(
            new MinecraftBlockEntityVoxel("minecraft:beacon", "minecraft:beacon", null, null),
            MinecraftBlockEntityVoxel.fromBlockEntity(beaconEntity, new MinecraftVoxel("minecraft:beacon"))
        );

        CompoundTag jukeboxEntity = new CompoundTag();
        jukeboxEntity.putString("id", "minecraft:jukebox");
        jukeboxEntity.putBoolean("keepPacked", false);
        jukeboxEntity.putInt("x", 1);
        jukeboxEntity.putInt("y", 2);
        jukeboxEntity.putInt("z", 3);
        CompoundTag item = new CompoundTag();
        item.putString("id", "minecraft:music_disc_cat");
        jukeboxEntity.put("RecordItem", item);
        jukeboxEntity.putLong("ticks_since_song_started", 0);

        CompoundTag data = new CompoundTag();
        data.put("RecordItem", item);
        data.putLong("ticks_since_song_started", 0);
        assertEquals(
            new MinecraftBlockEntityVoxel("minecraft:jukebox", "minecraft:jukebox", Map.of("has_record", "true"), data),
            MinecraftBlockEntityVoxel.fromBlockEntity(jukeboxEntity, new MinecraftVoxel("minecraft:jukebox", Map.of("has_record", "true")))
        );
    }

    @Test
    public void testPlaceSimple() {
        MinecraftVoxelTileMock tile = new MinecraftVoxelTileMock(new WorldBBox3d(
            new WorldCoords3d(-50, -10, 0),
            new WorldCoords3d(10, 0, 200)
        ));

        MinecraftBlockEntityVoxel beacon = new MinecraftBlockEntityVoxel("minecraft:beacon", "minecraft:beacon", null, null);
        beacon.place(tile, 3, -7, 64);

        CompoundTag expectedBeaconState = new CompoundTag();
        expectedBeaconState.putString("Name", "minecraft:beacon");
        tile.assertBlockStateAt(3, 64, 6, expectedBeaconState); // X/Y/Z => X/Z/-Y

        CompoundTag expectedBeaconEntity = new CompoundTag();
        expectedBeaconEntity.putString("id", "minecraft:beacon");
        expectedBeaconEntity.putBoolean("keepPacked", false);
        // X/Y/Z => X/Z/-Y
        expectedBeaconEntity.putInt("x", 3);
        expectedBeaconEntity.putInt("y", 64);
        expectedBeaconEntity.putInt("z", 6);
        tile.assertBlockEntityAt(3, 64, 6, expectedBeaconEntity); // X/Y/Z => X/Z/-Y
    }

    @Test
    public void testPlaceWithData() {
        MinecraftVoxelTileMock tile = new MinecraftVoxelTileMock(new WorldBBox3d(
            new WorldCoords3d(-50, -10, 0),
            new WorldCoords3d(10, 0, 200)
        ));

        CompoundTag data = new CompoundTag();
        CompoundTag item = new CompoundTag();
        item.putString("id", "minecraft:music_disc_cat");
        data.put("RecordItem", item);
        data.putLong("ticks_since_song_started", 0);
        MinecraftBlockEntityVoxel jukebox = new MinecraftBlockEntityVoxel("minecraft:jukebox", "minecraft:jukebox", Map.of("has_record", "true"), data);
        jukebox.place(tile, -43, 0, 192);

        CompoundTag expectedJukeboxState = new CompoundTag();
        expectedJukeboxState.putString("Name", "minecraft:jukebox");
        CompoundTag properties = new CompoundTag();
        properties.putString("has_record", "true");
        expectedJukeboxState.put("Properties", properties);
        tile.assertBlockStateAt(-43, 192, -1, expectedJukeboxState); // X/Y/Z => X/Z/-Y

        CompoundTag expectedJukeboxEntity = new CompoundTag();
        expectedJukeboxEntity.putString("id", "minecraft:jukebox");
        expectedJukeboxEntity.putBoolean("keepPacked", false);
        expectedJukeboxEntity.put("RecordItem", item);
        expectedJukeboxEntity.putLong("ticks_since_song_started", 0);
        // X/Y/Z => X/Z/-Y
        expectedJukeboxEntity.putInt("x", -43);
        expectedJukeboxEntity.putInt("y", 192);
        expectedJukeboxEntity.putInt("z", -1);
        tile.assertBlockEntityAt(-43, 192, -1, expectedJukeboxEntity); // X/Y/Z => X/Z/-Y
    }

    @Test
    public void testPlaceIllegal() {
        MinecraftBlockEntityVoxel beacon = new MinecraftBlockEntityVoxel("minecraft:beacon", "minecraft:beacon", null, null);
        assertThrows(IllegalArgumentException.class, () -> beacon.place(new TestingVoxelTile(WorldBBox3d.EMPTY), 0, 0, 0));
    }
}
