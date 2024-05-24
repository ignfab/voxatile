package com.ignfab.minalac.generator.outputs.minetest.utils;

import com.ignfab.minalac.generator.outputs.minetest.Block;
import com.ignfab.minalac.generator.world.MapWriteException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class SqlLiteMapWriter {
    private Connection connection;
    private Serializer serializer;

    public SqlLiteMapWriter(String directoryFullPath) throws MapWriteException {
        if (!Files.exists(Paths.get(directoryFullPath)))
            throw new MapWriteException("The directory can not be accessed");
        createAndConnectToFileDB(directoryFullPath);
        this.serializer = new Serializer();
    }

    private void createAndConnectToFileDB(String directoryFullPath) throws MapWriteException {
        try {
            String pathDBFile = "jdbc:sqlite:" + directoryFullPath + "map.sqlite";
            connection = DriverManager.getConnection(pathDBFile);
            Statement statement = connection.createStatement();
            statement.execute("PRAGMA synchronous=OFF");
            statement.execute("CREATE TABLE `blocks` (`pos` INT NOT NULL PRIMARY KEY,`data` BLOB)");
        } catch (SQLException e) {
            throw new MapWriteException(e.getMessage());
        }
    }

    public void insertBlock(int pos, Block block) throws MapWriteException {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO blocks VALUES (?,?)");
            statement.setInt(1, pos);
            statement.setBytes(2, this.serializer.serialize(block));
            statement.execute();
        } catch (SQLException | IOException e) {
            throw new MapWriteException("Failed to insert blocks into map : " + e.getMessage());
        }
    }
}