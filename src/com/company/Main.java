package com.company;

import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import javax.print.Doc;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);

        Spark.get(
                "/",
                (request,response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = selectUser(conn,name);
                    HashMap m = new HashMap<>();
                    if (user != null) {
                        m.put("name",user.name);
                    }
                    m.put("doctors",selectDoctor(conn));
                    return new ModelAndView(m,"home.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                (request,response) -> {
                    String name = request.queryParams("loginName");
                    String password = request.queryParams("password");
                    User user = selectUser(conn,name);
                    if (user == null) {
                        insertUser(conn,name,password);
                    }
                    else if (!password.equals(user.password)) {
                        return null;
                    }
                    Session session = request.session();
                    session.attribute("loginName",name);
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post(
                "/logout",
                (request,response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post(
                "/create-doctor",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = selectUser(conn,name);
                    if (user == null) {
                        response.redirect("/");
                        return null;
                    }
                    String docName = request.queryParams("docName");
                    String docSpec = request.queryParams("docSpec");
                    String docAddr = request.queryParams("docAddr");
                    int docCost = Integer.valueOf(request.queryParams("docCost"));
                    insertDoctor(conn,docName,docSpec,docAddr,docCost,user.id);
                    response.redirect("/");
                    return null;
                }
        );

        Spark.get(
                "/edit-doctor",
                (request, response) -> {
                    int id = Integer.valueOf(request.queryParams("id"));
                    return new ModelAndView(id,"edit.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/edit-doctor",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = selectUser(conn,name);
                    if (user == null) {
                        return null;
                    }
                    String docName = request.queryParams("docName");
                    String docSpec = request.queryParams("docSpec");
                    String docAddr = request.queryParams("docAddr");
                    int docCost = Integer.valueOf(request.queryParams("docCost"));
                    editDoctor(conn,docName,docSpec,docAddr,docCost,user.id);
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post(
                "/delete-doctor",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = selectUser(conn,name);
                    if (user == null) {
                        return null;
                    }
                    int id = Integer.valueOf(request.queryParams("id"));
                    deleteDoctor(conn,id);
                    response.redirect("/");
                    return null;
                }
        );
    }

    public static void createTables (Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS doctors (id IDENTITY,name " +
                "VARCHAR,specialty VARCHAR,address VARCHAR,cost INT,user_id INT)");
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
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM doctors " +
                "INNER JOIN users ON doctors.user_id = users.id");
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

    public static Doctor editDoctor (Connection conn,String name,
                                     String specialty,String address,int cost,int userId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE doctors SET id = null, name = ?," +
                "specialty = ?,address = ?,cost = ?,user_id = ?");
        stmt.setString(1,name);
        stmt.setString(2,specialty);
        stmt.setString(3,address);
        stmt.setInt(4,cost);
        stmt.setInt(5,userId);
        stmt.execute();
        return new Doctor(name,specialty,address,cost);
    }

    public static void deleteDoctor (Connection conn,int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM doctors WHERE id = ?");
        stmt.setInt(1,id);
        stmt.execute();
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
