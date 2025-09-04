package com.ignfab.minalac.generator.outputs.minetest.utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.ignfab.minalac.generator.outputs.minetest.Block;
import com.ignfab.minalac.generator.world.MapWriteException;

/**
 * {@code SQLiteMapWriter} is responsible for creating and updating the {@code map.sqlite} file,
 * which is used by Minetest to store all blocks of a world.
 * @see <a href="https://github.com/minetest/minetest/blob/master/doc/world_format.md#mapsqlite-1">Minetest's map world format</a>
 */
public class SQLiteMapWriter implements AutoCloseable {
    private final Connection connection;
    private final Serializer serializer;

    /**
     * Constructs a new {@code SQLiteMapWriter}.
     * It also creates and initializes {@code map.sqlite}.
     *
     * @param file database file to connect to (or create).
     * @throws MapWriteException if an {@link SQLException} occurs
     */
    public SQLiteMapWriter(File file) throws MapWriteException {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA synchronous=OFF");
            }
        } catch (SQLException e) {
            throw new MapWriteException(e);
        }
        serializer = new Serializer();
    }

    /**
     * Creates a new database.
     *
     * Must be called once before any {@code insertBlock}.
     */
    public void createDatabase() throws MapWriteException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE `blocks` (`pos` INT NOT NULL PRIMARY KEY, `data` BLOB)");
        } catch (SQLException e) {
            throw new MapWriteException(e);
        }
    }

    /**
     * Inserts the specified block into the {@code blocks} table in {@code map.sqlite} which contains all blocks of the world.
     *
     * @param pos the position hash associated to the block
     * @param block the {@link Block} to be inserted
     * @throws MapWriteException if an {@link SQLException} or {@link IOException} occurs during insertion
     */
    public void insertBlock(long pos, Block block) throws MapWriteException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO blocks VALUES (?, ?)")) {
            statement.setLong(1, pos);
            statement.setBytes(2, this.serializer.serialize(block));
            statement.execute();
        } catch (SQLException | IOException e) {
            throw new MapWriteException("Failed to insert blocks %d into map".formatted(pos), e);
        }
    }

    @Override
    public void close() throws Exception {
        serializer.close();
        connection.close();
    }
}
