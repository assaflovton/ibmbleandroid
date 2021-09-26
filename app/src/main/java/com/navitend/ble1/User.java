package com.navitend.ble1;

import java.util.ArrayList;

public class User {
    public String name;
    public String email;
    public ArrayList<SampleData> samples;

    public User(){}// do not delete, firebase uses it

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.samples = new ArrayList<>();
    }
}
