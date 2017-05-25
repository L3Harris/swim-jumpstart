/*
 * Copyright (c) 2020 L3Harris Technologies
 */
package com.l3harris.swim.outputs.custom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Scanner;

public class SqlHelper {
    private static Logger logger = LoggerFactory.getLogger(SqlHelper.class);

    static String readFile(String path) throws IOException {
        InputStream input = SqlHelper.class.getResourceAsStream(path);

        if (input == null) {
            // Fall back to the classloader
            input = SqlHelper.class.getClassLoader().getResourceAsStream(path);
        }

        Scanner s = new Scanner(input).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static void executeQueryFromFile(Connection connection, String path) throws SQLException {
        try {
            Statement s = connection.createStatement();
            String sql = SqlHelper.readFile(path);
            s.executeUpdate(sql);
            logger.debug("query successful");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Timestamp fromGregorianCalendar(XMLGregorianCalendar date) {
        return date == null ? null : new Timestamp(date.toGregorianCalendar().getTime().getTime());
    }

}
