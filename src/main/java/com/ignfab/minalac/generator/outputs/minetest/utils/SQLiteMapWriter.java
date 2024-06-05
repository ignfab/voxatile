package com.ignfab.minalac.generator.outputs.minetest.utils;

import com.ignfab.minalac.generator.outputs.minetest.Block;
import com.ignfab.minalac.generator.world.MapWriteException;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteMapWriter {
    private final Connection connection;
    private final Serializer serializer;

    public SQLiteMapWriter(File directory) throws MapWriteException {
        if (!directory.exists() || !directory.isDirectory())
            throw new MapWriteException("The directory can not be accessed");
        connection = createAndConnectToFileDB(new File(directory, "map.sqlite"));
        serializer = new Serializer();
    }

    private Connection createAndConnectToFileDB(File database) throws MapWriteException {
        try {
            String pathDBFile = "jdbc:sqlite:" + database.getAbsolutePath();
            Connection connection = DriverManager.getConnection(pathDBFile);
            Statement statement = connection.createStatement();
            statement.execute("PRAGMA synchronous=OFF");
            statement.execute("CREATE TABLE `blocks` (`pos` INT NOT NULL PRIMARY KEY,`data` BLOB)");
            return connection;
        } catch (SQLException e) {
            throw new MapWriteException(e);
        }
    }

    public void insertBlock(int pos, Block block) throws MapWriteException {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO blocks VALUES (?,?)");
            statement.setInt(1, pos);
            statement.setBytes(2, this.serializer.serialize(block));
            statement.execute();
        } catch (SQLException | IOException e) {
            throw new MapWriteException("Failed to insert blocks into map", e);
        }
    }
}
