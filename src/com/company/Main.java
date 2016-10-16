package com.company;

import org.h2.tools.Server;

import javax.print.Doc;
import java.sql.*;
import java.util.ArrayList;

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

    public static void insertDoctor (Connection conn,String name,
                                     String specialty,String address,int cost,int userId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO doctors VALUES (null,?,?,?,?,?)");
        stmt.setString(1,name);
        stmt.setString(2,specialty);
        stmt.setString(3,address);
        stmt.setInt(4,cost);
        stmt.setInt(5,userId);
        stmt.execute();
    }

    public static ArrayList<Doctor> selectDoctor (Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM doctors INNER JOIN users" +
                "ON doctors.user_id = users.id");
        ResultSet results = stmt.executeQuery();
        ArrayList<Doctor> doctors = new ArrayList<>();
        while(results.next()) {
            int id = results.getInt("doctors.id");
            String name = results.getString("doctors.name");
            String specialty = results.getString("doctors.specialty");
            String address = results.getString("doctors.address");
            int cost = results.getInt("doctors.cost");
            String author = results.getString("users.name");
            Doctor doctor = new Doctor(id,name,specialty,address,cost,author);
            doctors.add(doctor);
        }
        return doctors;
    }

    public static void insertUser(Connection conn,String name,String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES(null,?,?)");
        stmt.setString(1,name);
        stmt.setString(2,password);
        stmt.execute();
    }

    public static User selectUser (Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
        stmt.setString(1,name);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("id");
            String password = results.getString("password");
            return new User(id,name,password);
        }
        return null;
    }
}
