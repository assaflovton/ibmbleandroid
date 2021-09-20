package com.navitend.ble1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class User {
    public String name;
    public String email;
    public ArrayList<SampleData> samples;

    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.samples = new ArrayList<SampleData>();
    }
    public User(User u) {
        this.name = u.name;
        this.email = u.email;
        this.samples = new ArrayList<SampleData>();
        if (u.samples != null){
        for (SampleData s : u.samples
             ) {
            this.samples.add(new SampleData(s));
        }
        }
    }
}
