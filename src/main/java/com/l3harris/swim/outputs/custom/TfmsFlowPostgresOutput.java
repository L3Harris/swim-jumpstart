/*
 * Copyright (c) 2020 L3Harris Technologies
 */
package com.l3harris.swim.outputs.custom;

import com.l3harris.swim.outputs.Output;
import com.l3harris.swim.outputs.custom.tfmsFlow.AirportConfigurationMessage;
import com.l3harris.swim.outputs.custom.tfmsFlow.RestrictionMessage;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.gov.dot.faa.atm.tfm.tfmdataservice.TfmDataService;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TfmsFlowPostgresOutput extends Output {
    private static Logger logger = LoggerFactory.getLogger(TfmsFlowPostgresOutput.class);

    private JAXBContext jaxbContext;
    private Connection connection;

    public TfmsFlowPostgresOutput(Config config) {
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
            SqlHelper.executeQueryFromFile(connection, "sql/rstr.sql");
            SqlHelper.executeQueryFromFile(connection, "sql/aptc.sql");
            logger.info("Created the postgres tables");
        } catch (Exception e) {
            logger.error("Failed to connect and/or setup postgres", e);
            System.exit(-1);
        }


        try {
            jaxbContext = JAXBContext.newInstance("us.gov.dot.faa.atm.tfm.tfmdataservice");
        } catch (JAXBException e) {
            logger.error("Failed to create the JAXBContext", e);
            System.exit(-1);
        }
    }

    @Override
    public void output(String message) {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            TfmDataService tfmDataService = (TfmDataService) unmarshaller
                    .unmarshal(XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(message)));

            switch (tfmDataService.getFiOutput().getFiMessage().getMsgType()) {
                case RSTR:
                    new RestrictionMessage(connection).insert(tfmDataService.getFiOutput().getFiMessage());
                    break;
                case APTC:
                    new AirportConfigurationMessage(connection).insert(tfmDataService.getFiOutput().getFiMessage());
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
