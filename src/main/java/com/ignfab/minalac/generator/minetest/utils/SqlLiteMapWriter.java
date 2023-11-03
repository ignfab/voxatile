package com.ignfab.minalac.generator.minetest.utils;

import com.ignfab.minalac.generator.MapWriteException;
import com.ignfab.minalac.generator.minetest.Block;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class SqlLiteMapWriter {
    private Connection sqlFileConnector;
    private Serializer serializer;

    public SqlLiteMapWriter(String directoryFullPath) throws MapWriteException {
        if (!Files.exists(Paths.get(directoryFullPath)))
            throw new MapWriteException("The directory can not be accessed");
        this.sqlFileConnector = createAndConnectToFileDB(directoryFullPath);
        this.serializer = new Serializer();
    }

    private Connection createAndConnectToFileDB(String directoryFullPath) throws MapWriteException {
        Connection connection;
        try {
            String pathDBFile = "jdbc:sqlite:" + directoryFullPath + "map.sqlite";
            String statementTableBlock = "CREATE TABLE `blocks` (`pos` INT NOT NULL PRIMARY KEY,`data` BLOB);";
            connection = DriverManager.getConnection(pathDBFile);
            Statement createTable = connection.createStatement();
            createTable.execute(statementTableBlock);
        } catch (SQLException e) {
            throw new MapWriteException(e.getMessage());
        }
        return connection;
    }

    public void insertBlock(int pos, Block block) throws MapWriteException {
        try {
            PreparedStatement statement = sqlFileConnector.prepareStatement("INSERT INTO blocks VALUES (?,?)");
            statement.setInt(1, pos);
            statement.setBytes(2, this.serializer.serialize(block));
            statement.execute();
        } catch (SQLException | IOException e) {
            throw new MapWriteException("Failed to insert blocks into map : " + e.getMessage());
        }
    }
}