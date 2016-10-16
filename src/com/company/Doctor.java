package com.company;

/**
 * Created by Troy on 10/16/16.
 */
public class Doctor {
    int id;
    String name;
    String specialty;
    String address;
    int cost;
    String author;

    public Doctor(int id, String name, String specialty, String address, int cost, String author) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.address = address;
        this.cost = cost;
        this.author = author;
    }
}
