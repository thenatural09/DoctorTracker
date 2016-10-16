package com.company;

import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);
    }

    public static void createTables (Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS doctors (id IDENTITY,name VARCHAR,specialty" +
                "VARCHAR,address VARCHAR,cost INT,user_id INT)");
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY,name VARCHAR,password VARCHAR)");
    }
}
