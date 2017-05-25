/*
 * Copyright (c) 2020 L3Harris Technologies
 */
package com.l3harris.swim.outputs.database;

import com.l3harris.swim.outputs.Output;
import com.typesafe.config.Config;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class PostgresOutput extends Output {
    private static Logger logger = LoggerFactory.getLogger(PostgresOutput.class);

    private Connection connection;
    private String sql = "INSERT INTO MESSAGES VALUES (?::JSON)";
    private PreparedStatement ps;

    public PostgresOutput(Config config) {
        super(config);
        Config postgresConfig = config.getConfig("postgres");

        try {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection(
                    postgresConfig.getString("uri"),
                    postgresConfig.getString("user"),
                    postgresConfig.getString("password"));
            ps = connection.prepareStatement(sql);

            logger.info("Connection to postgres successful");

            Statement s = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS MESSAGES(message JSON)";
            s.executeUpdate(sql);
        } catch (Exception e) {
            logger.error("Failed to connect and/or setup postgres", e);
            System.exit(-1);
        }

        logger.info("Table created successfully");
    }

    @Override
    public void output(String message) {
        JSONObject xmlJSONObj = XML.toJSONObject(message);

        // TODO only add fields required
        try {
            ps.setObject(1,  xmlJSONObj.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to insert message into postgres", e);
        }
    }
}
