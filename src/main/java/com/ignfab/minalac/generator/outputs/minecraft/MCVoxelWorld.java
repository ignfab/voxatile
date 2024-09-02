package com.ignfab.minalac.generator.outputs.minecraft;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.querz.mca.Chunk;
import net.querz.mca.MCAUtil;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.FloatTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;

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
    private final Map<Integer, Region> regions;
    // Note: The two z-component values aren't strictly hard-limits.
    // We can extends from -2032 to 2031 but the client
    // will need higher performances to play the game!
    // The Querz library does not support extended limits...
    private static final WorldBBox3d MAX_LIMIT = new WorldBBox3d(
        new WorldCoords3d(-30_000_000, -30_000_000, 0),
        new WorldCoords3d(30_000_000, 30_000_000, 255)
    );

    /**
     * Constructs a new {@code MCVoxelWorld}.
     * The limits of the world have to be set using {@link #setLimits(WorldBBox3d)}
     */
    public MCVoxelWorld() {
        super(new VoxelWorldMetadata());
        regions = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldBBox3d maxLimits() {
        return MAX_LIMIT;
    }

    /**
     * {@inheritDoc}
     * The world is exported in a format for Minecraft.
     */
    @Override
    public void save(File destination) throws MapWriteException {
        try {
            NBTUtil.write(createLevelData(), new File(destination, "level.dat"), true);
        } catch (IOException e) {
            throw new MapWriteException("Unable to save level.dat", e);
        }
        File regionDirectory = new File(destination, "region");
        regionDirectory.mkdir();
        for (Region region : regions.values()) {
            try {
                region.save(regionDirectory);
            } catch (IOException e) {
                throw new MapWriteException("Unable to save region " + region.getFileName(), e);
            }
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

                data.putInt("DataVersion", Chunk.DEFAULT_DATA_VERSION);

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
                    // Commented out gamerule were added after 1.16.1
                    gameRules.putString("announceAdvancements", "true");
                    // gameRules.putString("blockExplosionDropDecay", "true"); // 1.19.3
                    gameRules.putString("commandBlockOutput", "true");
                    // gameRules.putString("commandModificationBlockLimit", "32768"); // 1.19.4
                    gameRules.putString("disableElytraMovementCheck", "false");
                    gameRules.putString("disableRaids", "false");
                    gameRules.putString("doDaylightCycle", "true");
                    gameRules.putString("doEntityDrops", "true");
                    gameRules.putString("doFireTick", "false"); // Toggled to avoid accidents!
                    gameRules.putString("doImmediateRespawn", "false");
                    gameRules.putString("doInsomnia", "true");
                    gameRules.putString("doLimitedCrafting", "false");
                    gameRules.putString("doMobLoot", "true");
                    gameRules.putString("doMobSpawning", "false");
                    gameRules.putString("doPatrolSpawning", "true");
                    gameRules.putString("doTileDrops", "true");
                    gameRules.putString("doTraderSpawning", "true");
                    // gameRules.putString("doVinesSpread", "true"); // 1.19.4
                    gameRules.putString("doWeatherCycle", "true");
                    // gameRules.putString("doWardenSpawning", "true"); // 1.19
                    gameRules.putString("drowningDamage", "true");
                    // gameRules.putString("enderPearlsVanishOnDeath", "true"); // 1.20.2
                    gameRules.putString("fallDamage", "true");
                    gameRules.putString("fireDamage", "true");
                    gameRules.putString("forgiveDeadPlayers", "true");
                    // gameRules.putString("freezeDamage", "true"); // 1.17
                    // gameRules.putString("globalSoundEvents", "true"); // 1.19.3
                    gameRules.putString("keepInventory", "false");
                    // gameRules.putString("lavaSourceConversion", "false"); // 1.19.3
                    gameRules.putString("logAdminCommands", "true");
                    gameRules.putString("maxCommandChainLength", "65536");
                    // gameRules.putString("maxCommandForkCount", "65536"); // 1.20.3
                    gameRules.putString("maxEntityCramming", "24");
                    // gameRules.putString("mobExplosionDropDecay", "true"); // 1.19.3
                    gameRules.putString("mobGriefing", "true");
                    gameRules.putString("naturalRegeneration", "true");
                    // gameRules.putString("playersNetherPortalCreativeDelay", "1"); // 1.20.3
                    // gameRules.putString("playersNetherPortalDefaultDelay", "80"); // 1.20.3
                    // gameRules.putString("playersSleepingPercentage", "100"); // 1.17
                    // gameRules.putString("projectilesCanBreakBlocks", "true"); // 1.20.3
                    gameRules.putString("randomTickSpeed", "3");
                    gameRules.putString("reducedDebugInfo", "false");
                    gameRules.putString("sendCommandFeedback", "true");
                    gameRules.putString("showDeathMessages", "true");
                    // gameRules.putString("snowAccumulationHeight", "1"); // 1.19.3
                    // gameRules.putString("spawnChunkRadius", "2"); // 1.20.5
                    gameRules.putString("spawnRadius", "10");
                    gameRules.putString("spectatorsGenerateChunks", "true");
                    // gameRules.putString("tntExplosionDropDecay", "false"); // 1.19.3
                    gameRules.putString("universalAnger", "false");
                    // gameRules.putString("waterSourceConversion", "true"); // 1.19.3
                }
                data.put("GameRules", gameRules);

                data.putInt("GameType", 1);

                // Legacy (1.15 and below)
                // data.putString("generatorName", "default");
                // data.putInt("generatorVersion", 0);
                // data.putString("generatorOptions", "generatorOptions");

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

                    player.putInt("DataVersion", Chunk.DEFAULT_DATA_VERSION);

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
                    version.putInt("Id", Chunk.DEFAULT_DATA_VERSION);
                    version.putString("Name", "1.16.1");
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
                                    biomeSource.putBoolean("large_biomes", false);
                                    biomeSource.putLong("seed", seed);
                                    biomeSource.putString("type", "minecraft:vanilla_layered");
                                }
                                generator.put("biome_source", biomeSource);

                                generator.putLong("seed", seed);
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
                                    biomeSource.putLong("seed", seed);
                                    biomeSource.putString("type", "minecraft:the_end");
                                }
                                generator.put("biome_source", biomeSource);

                                generator.putLong("seed", seed);
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
                                    biomeSource.putLong("seed", seed);
                                    biomeSource.putString("type", "minecraft:multi_noise");
                                }
                                generator.put("biome_source", biomeSource);

                                generator.putLong("seed", seed);
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

    // In-Game coords
    /* package-private */ synchronized void setBlockState(int blockX, int blockY, int blockZ, CompoundTag block) {
        // This method is synchronized as a workaround because the Querz library is not thread-safe.
        // This is a performance-killer!
        // The only part that really needs synchronization is inside nbt.querz.mca.Section:
        // During a palette update (adjustBlockStateBits), it should block, until operation
        // is complete, any other thread trying to add a block to the palette (addToPalette,
        // after the check for existing block inside palette).

        if (isOutOfLimits(blockX, blockY, blockZ)) return;
        getOrCreateRegion(blockX, blockZ).file().setBlockStateAt(blockX, blockY, blockZ, block, false);
    }

    // In-Game coords
    /* package-private */ void addBlockEntity(int blockX, int blockY, int blockZ, CompoundTag block)  {
        if (isOutOfLimits(blockX, blockY, blockZ)) return;
        Chunk chunk = getOrCreateRegion(blockX, blockZ).getOrCreateChunk(MCAUtil.blockToChunk(blockX), MCAUtil.blockToChunk(blockZ));
        ListTag<CompoundTag> blockEntities = chunk.getTileEntities();
        if (blockEntities == null) {
            blockEntities = new ListTag<>(CompoundTag.class);
            chunk.setTileEntities(blockEntities);
        }
        blockEntities.add(block);
    }

    // In-Game coords
    private Region getOrCreateRegion(int blockX, int blockZ)  {
        int regionX = MCAUtil.blockToRegion(blockX);
        int regionZ = MCAUtil.blockToRegion(blockZ);
        int key = computeRegionKey(regionX, regionZ);
        Region region = regions.get(key);
        if (region == null) {
            region = new Region(regionX, regionZ);
            regions.put(key, region);
        }
        return region;
    }

    // In-Game coords
    private int computeRegionKey(int regionX, int regionZ) {
        return (regionX << 16) | (regionZ & 0xFFFF);
    }

    // In-Game coords
    /* package-private */ boolean isOutOfLimits(int blockX, int blockY, int blockZ) {
        // (In-Game coords to world coords) X/Z/-Y => X/Y/Z
        return !limits().contains(blockX, -(blockZ + 1), blockY);
    }
}
