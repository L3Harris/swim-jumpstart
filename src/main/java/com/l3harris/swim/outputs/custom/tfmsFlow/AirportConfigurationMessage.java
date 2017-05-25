/*
 * Copyright (c) 2020 L3Harris Technologies
 */
package com.l3harris.swim.outputs.custom.tfmsFlow;

import com.l3harris.swim.outputs.custom.SqlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.gov.dot.faa.atm.tfm.ficommonmessages.AirportConfigType;
import us.gov.dot.faa.atm.tfm.flowinformation.FiOutputType;

import javax.xml.bind.JAXBElement;
import java.sql.*;

public class AirportConfigurationMessage {
    private static Logger logger = LoggerFactory.getLogger(AirportConfigurationMessage.class);

    private PreparedStatement ps;
    private String sql = "INSERT INTO APTC VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

    public AirportConfigurationMessage(Connection connection) {
        try {
            ps = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(FiOutputType.FiMessage message) throws SQLException {
        String sourceFacility = message.getSourceFacility();
        Timestamp sourceTimestamp = SqlHelper.fromGregorianCalendar(message.getSourceTimeStamp());
        String sensitivity = message.getSensitivity().value();

        // Parse the attributes
        ps.setString(1, sourceFacility);
        ps.setTimestamp(2, sourceTimestamp);
        ps.setString(3, sensitivity);

        // Parse the restriction
        AirportConfigType configMessage = message.getAirportConfigMessage();
        Timestamp eventTime = SqlHelper.fromGregorianCalendar(configMessage.getEventTime());
        Timestamp entryTime = SqlHelper.fromGregorianCalendar(configMessage.getEntryTime());
        String facility = configMessage.getFacility();
        String airport = configMessage.getAirport();
        String arrRunwayConf = configMessage.getArrRunwayConf().getValue();
        String depRunwayConf = configMessage.getDepRunwayConf().getValue();
        Integer arrRate = configMessage.getArrRate().getValue().intValue();
        Integer depRate = configMessage.getDepRate().getValue().intValue();
        Timestamp updateTime = SqlHelper.fromGregorianCalendar(configMessage.getUpdateTime());

        ps.setTimestamp(4, eventTime);
        ps.setTimestamp(5, entryTime);
        ps.setString(6, facility);
        ps.setString(7, airport);
        ps.setString(8, arrRunwayConf);
        ps.setString(9, depRunwayConf);
        ps.setInt(10, arrRate);
        ps.setInt(11, depRate);
        ps.setTimestamp(12, updateTime);



        // Execute the updates
        ps.executeUpdate();
    }

    private <T> T jaxbElementValue(JAXBElement<T> element) {
        return (element == null || element.isNil()) ? null : element.getValue();
    }
}
