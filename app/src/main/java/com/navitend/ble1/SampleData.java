package com.navitend.ble1;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

public class SampleData {
    public ArrayList<Float> data_y;
    public ArrayList<Integer> data_x;
    public ArrayList<Float> ms;
    public ArrayList<Integer> ms_time;
    public ArrayList<Float> hs;
    public ArrayList<Integer> hs_time;
    public ArrayList<Float> to;
    public ArrayList<Integer> to_time;
    public String date;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SampleData() {
        this.data_y = new ArrayList<Float>();
        this.data_x = new ArrayList<Integer>();
        this.ms = new ArrayList<Float>();
        this.ms_time = new ArrayList<Integer>();
        this.hs = new ArrayList<Float>();
        this.hs_time = new ArrayList<Integer>();
        this.to = new ArrayList<Float>();
        this.to_time = new ArrayList<Integer>();
        this.date = LocalDateTime.now().toString();
    }

    public SampleData(SampleData s) {
        this.data_y = new ArrayList<Float>();
        for (Float f : s.data_y
        ) {
            this.data_y.add(f);

        }
        this.data_x = new ArrayList<Integer>();
        for (Integer i : s.data_x
        ) {
            this.data_x.add(i);

        }
        this.ms = new ArrayList<Float>();
        for (Float f : s.ms
        ) {
            this.ms.add(f);

        }
        this.ms_time = new ArrayList<Integer>();
        for (Integer i : s.ms_time
        ) {
            this.ms_time.add(i);

        }
        this.hs = new ArrayList<Float>();
        for (Float f : s.hs
        ) {
            this.hs.add(f);

        }
        this.hs_time = new ArrayList<Integer>();
        for (Integer i : s.hs_time
        ) {
            this.hs_time.add(i);

        }
        this.to = new ArrayList<Float>();
        for (Float f : s.to
        ) {
            this.to.add(f);

        }
        this.to_time = new ArrayList<Integer>();
        for (Integer i : s.to_time
        ) {
            this.to_time.add(i);

        }
        this.date = s.date;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SampleData(ArrayList<Integer> x, ArrayList<Float> y,
                      ArrayList<Integer> ms_time_n, ArrayList<Float> ms_n,
                      ArrayList<Integer> hs_time_n, ArrayList<Float> hs_n,
                      ArrayList<Integer> to_time_n, ArrayList<Float> to_n) {
        this.data_y = new ArrayList<Float>();
        this.data_x = new ArrayList<Integer>();
        this.ms = new ArrayList<Float>();
        this.ms_time = new ArrayList<Integer>();
        this.hs = new ArrayList<Float>();
        this.hs_time = new ArrayList<Integer>();
        this.to = new ArrayList<Float>();
        this.to_time = new ArrayList<Integer>();
        for (Integer i : x) {
            data_x.add(i);
        }
        for (Float f : y) {
            data_y.add(f);
        }
        for (Integer i : ms_time_n) {
            ms_time.add(i);
        }
        for (Float f : ms_n) {
            ms.add(f);
        }
        for (Integer i : hs_time_n) {
            hs_time.add(i);
        }
        for (Float f : hs_n) {
            hs.add(f);
        }
        for (Integer i : to_time_n) {
            to_time.add(i);
        }
        for (Float f : to_n) {
            to.add(f);
        }
        String d = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(Calendar.getInstance().getTime());
        this.date = d.substring(0, d.length() - 6);// remove the Z from the format
    }

    public String getDate() {
        return date;
    }
}
