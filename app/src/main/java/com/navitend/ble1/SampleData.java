package com.navitend.ble1;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SampleData {
    public ArrayList<Float> data_y;
    public ArrayList<Integer> data_x;
    public String date;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SampleData() {
        this.data_y = new ArrayList<Float>();
        this.data_x = new ArrayList<Integer>();
        this.date =  LocalDateTime.now().toString();
    }

    public SampleData(SampleData s) {
        this.data_y = new ArrayList<Float>();
        for (Float f:s.data_y
             ) {
            this.data_y.add(f);

        }
        this.data_x = new ArrayList<Integer>();
        for (Integer i:s.data_x
        ) {
            this.data_x.add(i);

        }
        this.date =  s.date;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SampleData(ArrayList<Integer> x, ArrayList<Float> y){
        this.data_y = new ArrayList<Float>();
        this.data_x = new ArrayList<Integer>();
        for (Integer i : x) {
            data_x.add(i); }
        for (Float i : y) {
            data_y.add(i); }
        this.date =  new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z").format(Calendar.getInstance().getTime());

    }
}
