/*
 * Copyright (c) 2020 L3Harris Technologies
 */
package com.l3harris.swim.outputs.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.l3harris.swim.outputs.Output;
import com.l3harris.swim.outputs.custom.SqlHelper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.typesafe.config.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.jayway.jsonpath.Option.DEFAULT_PATH_LEAF_TO_NULL;
import static com.jayway.jsonpath.Option.SUPPRESS_EXCEPTIONS;

public class TDESPostgresOutput extends Output {
    private static Logger logger = LoggerFactory.getLogger(TDESPostgresOutput.class);

    private Connection connection;
    private PreparedStatement ps;

    private String sql = "INSERT INTO TDES " +
            "(airport, srcAddr, datisTime, header, body) " +
            "VALUES (?,?,?,?,?)";

    private Configuration config = Configuration.defaultConfiguration().addOptions(DEFAULT_PATH_LEAF_TO_NULL, SUPPRESS_EXCEPTIONS);
    XmlMapper xmlMapper = new XmlMapper();
    ObjectMapper objectMapper = new ObjectMapper();

    public TDESPostgresOutput(Config config) {
        super(config);
        Config postgresConfig = config.getConfig("postgres");

        try {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection(
                    postgresConfig.getString("uri"),
                    postgresConfig.getString("user"),
                    postgresConfig.getString("password"));

            logger.info("Connection to postgres successful");

            // Create the tables
            SqlHelper.executeQueryFromFile(connection, "sql/tdes.sql");
            logger.info("Created the postgres tables");

            try {
                ps = connection.prepareStatement(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            logger.error("Failed to connect and/or setup postgres", e);
            System.exit(-1);
        }
    }

    @Override
    public void output(String message) {
        try {
            String jsonObj = this.toJson(message);

            String airportID = JsonPath.using(config).parse(jsonObj).read("$.airportID");
            String srcAddr = JsonPath.using(config).parse(jsonObj).read("$.srcAddr");
            String datisTime = JsonPath.using(config).parse(jsonObj).read("$.DATISTime");
            String dataHeader = JsonPath.using(config).parse(jsonObj).read("$.dataHeader");
            String dataBody = JsonPath.using(config).parse(jsonObj).read("$.dataBody");

            if (airportID != null){
                ps.setString(1, airportID);
                ps.setString(2, srcAddr);
                ps.setString(3, datisTime);
                ps.setString(4, dataHeader);
                ps.setString(5, dataBody.replace(". ", ".\n"));

                ps.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    String toJson(String message) {
        String value = null;
        try {
            //Reading the XML
            JsonNode jsonNode = xmlMapper.readTree(message.getBytes());
            //Get JSON as a string
             value = objectMapper.writeValueAsString(jsonNode);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }
}
