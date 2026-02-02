package com.ignfab.minalac.generator.outputs.minecraft;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Random;

import io.github.ensgijs.nbt.io.BinaryNbtHelpers;
import io.github.ensgijs.nbt.io.CompressionType;
import io.github.ensgijs.nbt.mca.DataVersion;
import io.github.ensgijs.nbt.tag.CompoundTag;
import io.github.ensgijs.nbt.tag.DoubleTag;
import io.github.ensgijs.nbt.tag.FloatTag;
import io.github.ensgijs.nbt.tag.IntTag;
import io.github.ensgijs.nbt.tag.ListTag;
import io.github.ensgijs.nbt.tag.StringTag;

import com.ignfab.minalac.generator.generation.SquareUnitsTileGenerator;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.utils.world3d.WorldSize3d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.VoxelWorld;
import com.ignfab.minalac.generator.world.VoxelWorldMetadata;

/**
 * Implementation of {@link VoxelWorld} that creates a playable world specifically for Minecraft.
 */
public class MCVoxelWorld extends VoxelWorld {
    // Note: The two z-component values aren't strictly hard-limits.
    // We can extend from -2032 to 2031 but the client
    // will need higher performances to play the game!
    // The Querz library does not support extended limits...
    private static final WorldBBox3d MAX_LIMIT = new WorldBBox3d(
        new WorldCoords3d(-30_000_000, -30_000_000, -64),
        new WorldCoords3d(30_000_000, 30_000_000, 319)
    );

    private final File destination;
    private final File regionDirectory;

    /**
     * Constructs a new {@code MCVoxelWorld}.
     * The limits of the world have to be set using {@link #setLimits(WorldBBox3d)}
     *
     * @param destination Directory where to save data to. If null nothing is saved.
     */
    public MCVoxelWorld(File destination) {
        super(new VoxelWorldMetadata());
        this.destination = destination;

        if (destination == null)
            regionDirectory = null;
        else {
            regionDirectory = new File(destination, "region");
        }
    }

    @Override
    public WorldBBox3d maxLimits() {
        return MAX_LIMIT;
    }

    @Override
    public MCVoxelTile newTile(WorldBBox3d limits) {
        return new MCVoxelTile(regionDirectory, limits);
    }

    @Override
    public void initialize() throws MapWriteException {
        if (destination == null)
            return;

        if (!destination.exists() || !destination.isDirectory())
            throw new MapWriteException("Directory %s can not be accessed".formatted(destination));

        regionDirectory.mkdir();
    }

    /**
     * {@inheritDoc}
     * The world is exported in a format for Minecraft.
     */
    @Override
    public void finalizeAndSave() throws MapWriteException {
        if (destination == null)
            return; // Save disabled if null destination

        try {
            BinaryNbtHelpers.write(createLevelData(), new File(destination, "level.dat"), CompressionType.GZIP);
        } catch (IOException e) {
            throw new MapWriteException("Unable to save level.dat", e);
        }
    }

    @SuppressWarnings({ "checkstyle:MethodLength", "checkstyle:AvoidNestedBlocks", "checkstyle:LocalVariableName" })
    private CompoundTag createLevelData() {
        long seed = new Random().nextLong();

        CompoundTag root = new CompoundTag();
        {
            CompoundTag data = new CompoundTag();
            {
                data.putBoolean("allowCommands", true);
                WorldBBox3d bbox = limits();
                WorldSize3d size = limits().size();
                double minSize = Math.max(size.x(), size.y());
                data.putDouble("BorderCenterX", (bbox.minX() + bbox.maxX() + 1) / 2d);
                data.putDouble("BorderCenterZ", -(bbox.minY() + bbox.maxY() + 1) / 2d); // X/Y/Z => X/Z/-Y
                data.putDouble("BorderDamagePerBlock", 0.2);
                data.putDouble("BorderSafeZone", 5);
                data.putDouble("BorderSize", minSize);
                data.putDouble("BorderSizeLerpTarget", minSize);
                data.putLong("BorderSizeLerpTime", 0);
                data.putDouble("BorderWarningBlocks", 0);
                data.putDouble("BorderWarningTime", 15);

                data.putInt("clearWeatherTime", 0x7FFFFFFF);

                data.put("CustomBossEvents", new CompoundTag());

                CompoundTag dataPacks = new CompoundTag();
                {
                    dataPacks.put("Disabled", new ListTag<>(StringTag.class));
                    ListTag<StringTag> enabledDataPacks = new ListTag<>(StringTag.class);
                    enabledDataPacks.addString("vanilla");
                    dataPacks.put("Enabled", enabledDataPacks);
                }
                data.put("DataPacks", dataPacks);

                data.putInt("DataVersion", DataVersion.latest().id());

                data.putLong("DayTime", 0);

                data.putByte("Difficulty", (byte) 2);
                data.putBoolean("DifficultyLocked", false);

                CompoundTag dragonFight = new CompoundTag();
                {
                    dragonFight.putBoolean("DragonKilled", true);
                    ListTag<IntTag> gateways = new ListTag<>(IntTag.class);
                    {
                        for (int i = 0; i < 20; i++)
                            gateways.addInt(i);
                    }
                    dragonFight.put("Gateways", gateways);
                    dragonFight.putBoolean("PreviouslyKilled", true);
                }
                data.put("DragonFight", dragonFight);

                CompoundTag gameRules = new CompoundTag();
                {
                    gameRules.putBoolean("minecraft:advance_time", true);
                    gameRules.putBoolean("minecraft:advance_weather", true);
                    gameRules.putBoolean("minecraft:allow_entering_nether_using_portals", true);
                    gameRules.putBoolean("minecraft:block_drops", true);
                    gameRules.putBoolean("minecraft:block_explosion_drop_decay", true);
                    gameRules.putBoolean("minecraft:command_block_output", true);
                    gameRules.putBoolean("minecraft:command_blocks_work", true);
                    gameRules.putBoolean("minecraft:drowning_damage", true);
                    gameRules.putBoolean("minecraft:elytra_movement_check", true);
                    gameRules.putBoolean("minecraft:ender_pearls_vanish_on_death", true);
                    gameRules.putBoolean("minecraft:entity_drops", true);
                    gameRules.putBoolean("minecraft:fall_damage", true);
                    gameRules.putBoolean("minecraft:fire_damage", true);
                    gameRules.putInt("minecraft:fire_spread_radius_around_player", 0); // Modified
                    gameRules.putBoolean("minecraft:forgive_dead_players", true);
                    gameRules.putBoolean("minecraft:freeze_damage", true);
                    gameRules.putBoolean("minecraft:global_sound_events", true);
                    gameRules.putBoolean("minecraft:immediate_respawn", false);
                    gameRules.putBoolean("minecraft:keep_inventory", false);
                    gameRules.putBoolean("minecraft:lava_source_conversion", false);
                    gameRules.putBoolean("minecraft:limited_crafting", false);
                    gameRules.putBoolean("minecraft:locator_bar", true);
                    gameRules.putBoolean("minecraft:log_admin_commands", true);
                    gameRules.putInt("minecraft:max_block_modifications", 32768);
                    gameRules.putInt("minecraft:max_command_forks", 65536);
                    gameRules.putInt("minecraft:max_command_sequence_length", 65536);
                    gameRules.putInt("minecraft:max_entity_cramming", 24);
                    gameRules.putInt("minecraft:max_snow_accumulation_height", 1);
                    gameRules.putBoolean("minecraft:mob_drops", true);
                    gameRules.putBoolean("minecraft:mob_explosion_drop_decay", true);
                    gameRules.putBoolean("minecraft:mob_griefing", true);
                    gameRules.putBoolean("minecraft:natural_health_regeneration", true);
                    gameRules.putBoolean("minecraft:player_movement_check", true);
                    gameRules.putInt("minecraft:players_nether_portal_creative_delay", 0);
                    gameRules.putInt("minecraft:players_nether_portal_default_delay", 80);
                    gameRules.putInt("minecraft:players_sleeping_percentage", 100);
                    gameRules.putBoolean("minecraft:projectiles_can_break_blocks", true);
                    gameRules.putBoolean("minecraft:pvp", true);
                    gameRules.putBoolean("minecraft:raids", true);
                    gameRules.putInt("minecraft:random_tick_speed", 3);
                    gameRules.putBoolean("minecraft:reduced_debug_info", false);
                    gameRules.putInt("minecraft:respawn_radius", 10);
                    gameRules.putBoolean("minecraft:send_command_feedback", true);
                    gameRules.putBoolean("minecraft:show_advancement_messages", true);
                    gameRules.putBoolean("minecraft:show_death_messages", true);
                    gameRules.putBoolean("minecraft:spawn_mobs", false); // Modified
                    gameRules.putBoolean("minecraft:spawn_monsters", true);
                    gameRules.putBoolean("minecraft:spawn_patrols", true);
                    gameRules.putBoolean("minecraft:spawn_phantoms", true);
                    gameRules.putBoolean("minecraft:spawn_wandering_traders", true);
                    gameRules.putBoolean("minecraft:spawn_wardens", true);
                    gameRules.putBoolean("minecraft:spawner_blocks_work", true);
                    gameRules.putBoolean("minecraft:spectators_generate_chunks", true);
                    gameRules.putBoolean("minecraft:spread_vines", true);
                    gameRules.putBoolean("minecraft:tnt_explodes", true);
                    gameRules.putBoolean("minecraft:tnt_explosion_drop_decay", false);
                    gameRules.putBoolean("minecraft:universal_anger", false);
                    gameRules.putBoolean("minecraft:water_source_conversion", true);
                }
                data.put("game_rules", gameRules);

                data.putInt("GameType", 1);

                data.putBoolean("hardcore", false);

                data.putBoolean("initialized", true);

                data.putLong("LastPlayed", new Date().getTime());

                data.putString("LevelName", metadata.getWorldName());

                data.putBoolean("MapFeatures", false);

                CompoundTag player = new CompoundTag();
                {
                    CompoundTag abilities = new CompoundTag();
                    {
                        abilities.putBoolean("flying", true);
                        abilities.putFloat("flySpeed", 0.05f);
                        abilities.putBoolean("instabuild", true);
                        abilities.putBoolean("invulnerable", true);
                        abilities.putBoolean("mayBuild", true);
                        abilities.putBoolean("mayfly", true);
                        abilities.putFloat("walkSpeed", 0.1f);
                    }
                    player.put("abilities", abilities);

                    player.putFloat("AbsorptionAmount", 0);

                    player.putShort("Air", (short) 300);

                    ListTag<CompoundTag> attributes = new ListTag<>(CompoundTag.class);
                    {
                        CompoundTag movementSpeed = new CompoundTag();
                        {
                            movementSpeed.putDouble("Base", 0.1);
                            movementSpeed.putString("Name", "minecraft:generic.movement_speed");
                        }
                        attributes.add(movementSpeed);
                    }
                    player.put("Attributes", attributes);

                    CompoundTag brain = new CompoundTag();
                    {
                        brain.put("memories", new CompoundTag());
                    }
                    player.put("Brain", brain);

                    player.putInt("DataVersion", DataVersion.latest().id());

                    player.putShort("DeathTime", (short) 0);

                    player.putString("Dimension", "minecraft:overworld");

                    player.put("EnderItems", new ListTag<>(CompoundTag.class));

                    player.putFloat("FallDistance", 0);
                    player.putBoolean("FallFlying", false);

                    player.putShort("Fire", (short) -20);

                    player.putFloat("foodExhaustionLevel", 0);
                    player.putInt("foodLevel", 20);
                    player.putFloat("foodSaturationLevel", 5);
                    player.putInt("foodTickTimer", 0);

                    player.putFloat("Health", 20);
                    player.putInt("HurtByTimestamp", 0);
                    player.putShort("HurtTime", (short) 0);

                    player.put("Inventory", new ListTag<>(CompoundTag.class));

                    player.putBoolean("Invulnerable", false);

                    ListTag<DoubleTag> motion = new ListTag<>(DoubleTag.class);
                    {
                        motion.addDouble(0);
                        motion.addDouble(0);
                        motion.addDouble(0);
                    }
                    player.put("Motion", motion);

                    player.putBoolean("OnGround", false);

                    player.putInt("playerGameType", 1);

                    player.putInt("PortalCooldown", 0);

                    ListTag<DoubleTag> pos = new ListTag<>(DoubleTag.class);
                    {
                        // X/Y/Z => X/Z/-Y
                        pos.addDouble(metadata.getSpawn().x() + 0.5);
                        // TODO Replace by a better constraint management mechanism
                        // pos.addDouble(metadata.getSpawn().z());
                        pos.addDouble(Math.min(Math.max(limits().minZ(), metadata.getSpawn().z()), limits().maxZ()));
                        pos.addDouble(-(metadata.getSpawn().y() + 0.5));
                    }
                    player.put("Pos", pos);

                    player.putInt("previousPlayerGameType", -1);

                    ListTag<FloatTag> rotation = new ListTag<>(FloatTag.class);
                    {
                        rotation.addFloat(0);
                        rotation.addFloat(0);
                    }
                    player.put("Rotation", rotation);

                    CompoundTag recipeBook = new CompoundTag();
                    {
                        recipeBook.putBoolean("isBlastingFurnaceFilteringCraftable", false);
                        recipeBook.putBoolean("isBlastingFurnaceGuiOpen", false);
                        recipeBook.putBoolean("isFilteringCraftable", false);
                        recipeBook.putBoolean("isFurnaceFilteringCraftable", false);
                        recipeBook.putBoolean("isFurnaceGuiOpen", false);
                        recipeBook.putBoolean("isGuiOpen", false);
                        recipeBook.putBoolean("isSmokerFilteringCraftable", false);
                        recipeBook.putBoolean("isSmokerGuiOpen", false);

                        recipeBook.put("recipes", new ListTag<>(StringTag.class));
                        recipeBook.put("toBeDisplayed", new ListTag<>(StringTag.class));
                    }
                    player.put("recipeBook", recipeBook);

                    player.putInt("Score", 0);

                    player.putBoolean("seenCredits", false);

                    player.putInt("SelectedItemSlot", 0);

                    player.putShort("SleepTimer", (short) 0);

                    player.putIntArray("UUID", new int[] { 0, 0, 0, 0 });

                    player.putInt("XpLevel", 0);
                    player.putFloat("XpP", 0);
                    player.putInt("XpSeed", 0);
                    player.putInt("XpTotal", 0);
                }
                data.put("Player", player);

                data.put("ScheduledEvents", new ListTag<>(CompoundTag.class));

                ListTag<StringTag> serverBrands = new ListTag<>(StringTag.class);
                {
                    serverBrands.addString("vanilla");
                }
                data.put("ServerBrands", serverBrands);

                data.putBoolean("raining", false);
                data.putInt("rainTime", 0x7FFFFFFF);

                data.putLong("RandomSeed", seed);

                data.putLong("SizeOnDisk", 0); // Unused

                // X/Y/Z => X/Z/-Y
                data.putInt("SpawnX", metadata.getSpawn().x());
                // TODO Replace by a better constraint management mechanism
                // data.putInt("SpawnY", metadata.getSpawn().z());
                data.putInt("SpawnY", Math.min(Math.max(limits().minZ(), metadata.getSpawn().z()), limits().maxZ()));
                data.putInt("SpawnZ", -metadata.getSpawn().y() - 1);

                data.putBoolean("thundering", false);
                data.putInt("thunderTime", 0x7FFFFFFF);

                data.putLong("Time", 0);

                data.putInt("version", 19133);
                CompoundTag version = new CompoundTag();
                {
                    version.putInt("Id", DataVersion.latest().id());
                    version.putString("Name", DataVersion.latest().toSimpleString());
                    version.putString("Series", "main");
                    version.putBoolean("Snapshot", false);
                }
                data.put("Version", version);

                data.putInt("WanderingTraderSpawnChance", 25);
                data.putInt("WanderingTraderSpawnDelay", 24000);

                data.putBoolean("WasModded", false);

                CompoundTag worldGenSettings = new CompoundTag();
                {
                    worldGenSettings.putBoolean("bonus_chest", false);

                    CompoundTag dimensions = new CompoundTag();
                    {
                        CompoundTag overworld = new CompoundTag();
                        {
                            CompoundTag generator = new CompoundTag();
                            {
                                CompoundTag biomeSource = new CompoundTag();
                                {
                                    biomeSource.putString("preset", "minecraft:overworld");
                                    biomeSource.putString("type", "minecraft:multi_noise");
                                }
                                generator.put("biome_source", biomeSource);

                                generator.putString("settings", "minecraft:overworld");
                                generator.putString("type", "minecraft:noise");
                            }
                            overworld.put("generator", generator);

                            overworld.putString("type", "minecraft:overworld");
                        }
                        dimensions.put("minecraft:overworld", overworld);

                        CompoundTag the_end = new CompoundTag();
                        {
                            CompoundTag generator = new CompoundTag();
                            {
                                CompoundTag biomeSource = new CompoundTag();
                                {
                                    biomeSource.putString("type", "minecraft:the_end");
                                }
                                generator.put("biome_source", biomeSource);

                                generator.putString("settings", "minecraft:end");
                                generator.putString("type", "minecraft:noise");
                            }
                            the_end.put("generator", generator);

                            the_end.putString("type", "minecraft:the_end");
                        }
                        dimensions.put("minecraft:the_end", the_end);

                        CompoundTag the_nether = new CompoundTag();
                        {
                            CompoundTag generator = new CompoundTag();
                            {
                                CompoundTag biomeSource = new CompoundTag();
                                {
                                    biomeSource.putString("preset", "minecraft:nether");
                                    biomeSource.putString("type", "minecraft:multi_noise");
                                }
                                generator.put("biome_source", biomeSource);

                                generator.putString("settings", "minecraft:nether");
                                generator.putString("type", "minecraft:noise");
                            }
                            the_nether.put("generator", generator);

                            the_nether.putString("type", "minecraft:the_nether");
                        }
                        dimensions.put("minecraft:the_nether", the_nether);
                    }
                    worldGenSettings.put("dimensions", dimensions);

                    worldGenSettings.putBoolean("generate_features", false);

                    worldGenSettings.putLong("seed", seed);
                }
                data.put("WorldGenSettings", worldGenSettings);
            }
            root.put("Data", data);
        }
        return root;
    }

    @Override
    public Collection<WorldBBox2d> tiles(int maxTileSize) {
        SquareUnitsTileGenerator tileGenerator = new SquareUnitsTileGenerator(Region.SIZE, limits().to2d());
        return tileGenerator.getTiles(maxTileSize);
    }
}
