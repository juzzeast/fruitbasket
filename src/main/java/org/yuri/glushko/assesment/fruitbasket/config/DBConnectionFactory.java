package org.yuri.glushko.assesment.fruitbasket.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.yuri.glushko.assesment.fruitbasket.exceptions.DatabaseException;

// Connection factory
// Currently initiates in-memory DB & yields standard java.sql.Connection to it
// Subject to change in future releases
public class DBConnectionFactory {
    // It executes scripts that creates tables too
    // TODO: split creation & db structure set up
    public static String DB_URL = "jdbc:h2:mem:;INIT=runscript from 'classpath:create_tables.sql'";

    public static Connection getConnection() throws SQLException, DatabaseException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ce) {
            ce.printStackTrace();
            throw new DatabaseException("Can't create internal database: " + ce.getMessage());
        }
        Connection conn = DriverManager.getConnection(DB_URL,"sa","");

        return conn;
    }
}
