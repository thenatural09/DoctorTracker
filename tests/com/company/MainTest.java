package com.company;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Troy on 10/16/16.
 */
public class MainTest {
    public Connection startConnection () throws SQLException {
       Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
       Main.createTables(conn);
       return conn;
    }

    @Test
    public void testUser() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn,"Troy","pass");
        User user = Main.selectUser(conn,"Troy");
        conn.close();
        assertTrue(user != null);
    }

    @Test
    public void testDoctor() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn,"Troy","pass");
        Main.insertDoctor(conn,"Dr. Brown","brain surgery","723 Corporal St Dallas,TX",500,1);
        ArrayList<Doctor> doctors = Main.selectDoctor(conn);
        conn.close();
        assertTrue(!doctors.isEmpty());
    }
}