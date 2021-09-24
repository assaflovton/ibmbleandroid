package com.navitend.ble1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.*;

import java.util.function.Function;
import java.util.stream.Collectors;

public class SamplesActivity extends AppCompatActivity {
    private Spinner samples_spinner;
    private ImageView im;
    private String curr_email;
    private List<String> samples_dates ;
    private HashMap<String, SampleData> samples;
    //private GraphView graph;
    private XYPlot plot;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_samples);
        samples_spinner = findViewById(R.id.spinner);
        Intent intent = getIntent();
        curr_email = intent.getStringExtra("email");
        //im = findViewById(R.id.im);
        getSamplesData();
//        graph = (GraphView) findViewById(R.id.graph);
        // initialize our XYPlot reference:

        plot = (XYPlot) findViewById(R.id.plot);
        //drawGraph();


    }
 @RequiresApi(api = Build.VERSION_CODES.N)

 public void drawGraph(String key){
        //get rid of zeros that we sent for padding
     int last_index_of_not_zero = 0;
     for(last_index_of_not_zero = samples.get(key).data_y.size()-1; last_index_of_not_zero>=0; last_index_of_not_zero-- )
     {
         if(samples.get(key).data_x.get(last_index_of_not_zero) != 0){
             break;
         }
     }
     Number[] seriesNumbers = new Number[last_index_of_not_zero*2];

     for (int i =0; i<last_index_of_not_zero*2; i+=2) {
         seriesNumbers[i] = (Number)(samples.get(key).data_x.get(i/2) - samples.get(key).data_x.get(0));
         seriesNumbers[i+1] = (Number)samples.get(key).data_y.get(i/2);
     }

     XYSeries series1 = new SimpleXYSeries(
             Arrays.asList(seriesNumbers), SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Recorded data");

     // create formatters to use for drawing a series using LineAndPointRenderer
     // and configure them from xml:
     LineAndPointFormatter series1Format =
             new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels);




     series1Format.setInterpolationParams(
             new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));


     plot.addSeries(series1, series1Format);






 }
    public void getSamplesData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Users");
                rootRef.addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot Snapshot) {
                        for (DataSnapshot snapshot : Snapshot.getChildren()) {
                            if (snapshot.exists()) {
                                if (snapshot.getValue(User.class) != null && snapshot.getValue(User.class).email != null) {
                                    if (snapshot.getValue(User.class).email.equals(curr_email)) {
                                        User u = snapshot.getValue(User.class);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                samples = new HashMap<String, SampleData>();
                                                for (SampleData s: u.samples) {
                                                    samples.put(s.getDate().substring(0, s.getDate().length()-6), s);
                                                }
                                                samples_dates = new ArrayList<String>(samples.keySet());

                                                final ArrayAdapter<String> spinnerArrayAdapter =
                                                        new ArrayAdapter<String>(SamplesActivity.this,R.layout.spinner_item,samples_dates);

                                                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                                                samples_spinner.setAdapter(spinnerArrayAdapter);
                                                samples_spinner.setOnItemSelectedListener(
                                                        new AdapterView.OnItemSelectedListener() {
                                                            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                                                                Object item = parent.getItemAtPosition(pos);
                                                                Log.i("hey",item.toString());     //prints the text in spinner item.
                                                                //drawGraph(item.toString());
                                                                drawGraph(item.toString());
                                                                plot.setVisibility(View.GONE);
                                                                plot.setVisibility(View.VISIBLE);
                                                                Log.i("hey", "starting to draw graph");
                                                            }
                                                            public void onNothingSelected(AdapterView<?> parent) {
                                                            }
                                                        });


                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }
}