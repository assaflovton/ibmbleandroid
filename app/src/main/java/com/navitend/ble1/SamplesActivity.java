package com.navitend.ble1;

import static android.content.Intent.EXTRA_STREAM;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;

import android.app.AlertDialog;

public class SamplesActivity extends AppCompatActivity implements View.OnClickListener {
    private Spinner samples_spinner;
    private String curr_email;
    private ImageView share_im;
    private CheckBox ms_cb;
    private CheckBox hs_cb;
    private CheckBox to_cb;
    private ImageView trash_im;
    private List<String> samples_dates;
    private HashMap<String, SampleData> samples;
    private XYPlot plot; // the graph object
    final String empty = "There is no data recorded"; //the graph title if there are no samples
    String key = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_samples);
        // connect ui to code
        samples_spinner = findViewById(R.id.spinner);
        ms_cb = findViewById(R.id.MS);
        ms_cb.setOnClickListener(SamplesActivity.this);
        hs_cb = findViewById(R.id.HS);
        hs_cb.setOnClickListener(SamplesActivity.this);
        to_cb = findViewById(R.id.TO);
        to_cb.setOnClickListener(SamplesActivity.this);
        trash_im = findViewById(R.id.trash_im);
        trash_im.setOnClickListener(SamplesActivity.this);
        //add the scale animation
        buttonEffect(trash_im);
        share_im = findViewById(R.id.share);
        share_im.setOnClickListener(SamplesActivity.this);
        //add the scale animation
        buttonEffect(share_im);
        plot = findViewById(R.id.plot);
        // get the current logged in user from the past activity
        Intent intent = getIntent();
        curr_email = intent.getStringExtra("email");
        // real activity work
        getSamplesData();// connect to firebase and retrieve the data
        plot.getGraph().setPaddingLeft(50); // so Y axis won't cut off
        // so domain values will be round and nice
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 2500);
        if (key == null) {
            plot.setTitle(empty);// if there are no samples it will say that in the title
        }
        plot.setVisibility(View.GONE); // for changes to take place
        plot.setVisibility(View.VISIBLE);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    // gets a sample name and draw it
    public void drawGraph(String key) {
        plot.clear();//clear the view so it won't draw graph on previous graph
        // add the graph title
        if (samples.get(key).date != null) {
            plot.setTitle(samples.get(key).date);
        }
        // add the axis names
        plot.setRangeLabel("angular velocity [deg\\s]");
        plot.setDomainLabel("time [ms]");

        if (samples.get(key).data_x != null || samples.get(key).data_x.size() != 0) {

            Number[] seriesNumbers = new Number[samples.get(key).data_x.size() * 2];
            int freq_of_labels = 4;


            //make an interleaved list of the data [x1,y1,x2,y2....]
            for (int i = 0; i < samples.get(key).data_x.size() * 2; i += 2) {
                seriesNumbers[i] = (samples.get(key).data_x.get(i / 2) );
                seriesNumbers[i + 1] = samples.get(key).data_y.get(i / 2);
            }


            // create the arrays in the format androidplot works with
            XYSeries series = new SimpleXYSeries(
                    Arrays.asList(seriesNumbers), SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Data");

            LineAndPointFormatter seriesFormatData =
                    new LineAndPointFormatter(this, R.xml.line_point_formatter_with_no_labels);
            // add the data
            plot.addSeries(series, seriesFormatData);

        }

        if (samples.get(key).hs != null || samples.get(key).hs.size() != 0) {

            Number[] seriesNumbersHS = new Number[samples.get(key).hs.size() * 2];

            for (int i = 0; i < samples.get(key).hs.size() * 2; i += 2) {
                seriesNumbersHS[i] = (samples.get(key).hs_time.get(i / 2));
                seriesNumbersHS[i + 1] = samples.get(key).hs.get(i / 2);
            }

            XYSeries seriesHS = new SimpleXYSeries(
                    Arrays.asList(seriesNumbersHS), SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "HS");

            LineAndPointFormatter seriesFormatHS;

            if (hs_cb.isChecked()) {
                seriesFormatHS =
                        new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels_hs);
            } else {
                seriesFormatHS =
                        new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels_hs_no_labels);
            }
            plot.addSeries(seriesHS, seriesFormatHS);
        }

        if (samples.get(key).ms != null || samples.get(key).ms.size() != 0) {

            Number[] seriesNumbersMS = new Number[samples.get(key).ms.size() * 2];

            for (int i = 0; i < samples.get(key).ms.size() * 2; i += 2) {
                seriesNumbersMS[i] = (samples.get(key).ms_time.get(i / 2));
                seriesNumbersMS[i + 1] = samples.get(key).ms.get(i / 2);
            }

            XYSeries seriesMS = new SimpleXYSeries(
                    Arrays.asList(seriesNumbersMS), SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "MS");

            // create formatters, if labels is not check use the xml with transparent labels
            LineAndPointFormatter seriesFormatMS;
            if (ms_cb.isChecked()) {
                seriesFormatMS =
                        new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels_ms);
            } else {
                seriesFormatMS =
                        new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels_ms_no_labels);
            }
            plot.addSeries(seriesMS, seriesFormatMS);
        }
        if (samples.get(key).to != null || samples.get(key).to.size() != 0) {
            Number[] seriesNumbersTO = new Number[samples.get(key).to.size() * 2];
            for (int i = 0; i < samples.get(key).to.size() * 2; i += 2) {
                seriesNumbersTO[i] = (samples.get(key).to_time.get(i / 2));
                seriesNumbersTO[i + 1] = samples.get(key).to.get(i / 2);
            }

            XYSeries seriesTO = new SimpleXYSeries(
                    Arrays.asList(seriesNumbersTO), SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "TO");

            // create formatters, if labels is not check use the xml with transparent labels
            LineAndPointFormatter seriesFormatTO;
            if (to_cb.isChecked()) {
                seriesFormatTO =
                        new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels_to);
            } else {
                seriesFormatTO =
                        new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels_to_no_labels);
            }

            plot.addSeries(seriesTO, seriesFormatTO);

        }
    }

    // retrieve the data from firebase and handle drawing it when a sample is selected from the dropdown list
    public void getSamplesData() {
        runOnUiThread(() -> {
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
                                    runOnUiThread(() -> {
                                        if (u.samples == null) {
                                            return;
                                        }
                                        // map between the sample date and sample
                                        samples = new HashMap<>();
                                        for (SampleData s : u.samples) {
                                            samples.put(s.getDate(), s);
                                        }
                                        // take only the date for the dropdown list
                                        samples_dates = new ArrayList<>(samples.keySet());
                                        // sort to represent by date
                                        Collections.sort(samples_dates);
                                        // connect between data and ui
                                        final ArrayAdapter<String> spinnerArrayAdapter =
                                                new ArrayAdapter<>(SamplesActivity.this, R.layout.spinner_item, samples_dates);
                                        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                                        samples_spinner.setAdapter(spinnerArrayAdapter);
                                        // define what to do when an item from the list is selected
                                        samples_spinner.setOnItemSelectedListener(
                                                new AdapterView.OnItemSelectedListener() {
                                                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                                                        Object item = parent.getItemAtPosition(pos);
                                                        Log.i("Graph", "spinner selected sample: " + item.toString());
                                                        Log.i("Graph", "starting to draw graph");
                                                        key = item.toString(); //update the current key of the graph presented in the activity
                                                        drawGraph(key);
                                                        plot.setVisibility(View.GONE);
                                                        plot.setVisibility(View.VISIBLE);
                                                    }

                                                    public void onNothingSelected(AdapterView<?> parent) {
                                                    }
                                                });
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
        });
    }

    // the user selected to delete the sample that are currently presented in the activity, we can find it using key
    // finds the sample and remove it from the database than refreshes the activity so changes will take place
    public void deleteSample(String key) {
        runOnUiThread(() -> {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Users");
            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot Snapshot) {
                    for (DataSnapshot snapshot : Snapshot.getChildren()) {
                        if (snapshot.exists()) {
                            if (snapshot.getValue(User.class) != null && snapshot.getValue(User.class).email != null) {
                                if (snapshot.getValue(User.class).email.equals(curr_email)) {
                                    User u = snapshot.getValue(User.class);
                                    //find the sample to delete by the date (of the current presented graph)
                                    for (int i = 0; i < u.samples.size(); i++) {
                                        if (u.samples.get(i).getDate().equals(key)) {
                                            u.samples.remove(i);
                                            break;//found the sample to delete
                                        }
                                    }
                                    rootRef.child(snapshot.getKey()).setValue(u);//update value in the database
                                    Intent intent = getIntent();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); //remove blink animation
                                    finish();//we need to do this so the deletions will be visible
                                    startActivity(intent);

                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    //handle the delete icon and the label checkbox
    public void onClick(View view) {
        if (key == null) { // there is no graph nothing to do
            return;
        }
        switch (view.getId()) {
            // handle pressing the labels checkbox of MS
            case R.id.MS:
                // handle pressing the labels checkbox of HS
            case R.id.HS:
                // handle pressing the labels checkbox of TO
            case R.id.TO:
                drawGraph(key);//redraw the graph with or without labels
                plot.setVisibility(View.GONE);//refresh so the changes will take place
                plot.setVisibility(View.VISIBLE);
                break;
        }
    }

    //creates a text in a csv format ready to be shared as plain text
    private void shareAsTextPressed() throws FileNotFoundException, UnsupportedEncodingException {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String csv = "time, angular_velocity, time_MS, angular_velocity_MS, time_HS, angular_velocity_HS, time_TO, angular_velocity_TO\n";
        for (int i = 0; i < samples.get(key).data_x.size(); i++) {

            csv += samples.get(key).data_x.get(i).toString() + ", ";
            csv += samples.get(key).data_y.get(i).toString() + ",";
            if (i < samples.get(key).ms.size()) {
                csv += " " + samples.get(key).ms_time.get(i).toString() + ", ";
                csv += samples.get(key).ms.get(i).toString() + ",";
            } else {
                csv += ",,";
            }
            if (i < samples.get(key).hs.size()) {
                csv += " " + samples.get(key).hs_time.get(i).toString() + ", ";
                csv += samples.get(key).hs.get(i).toString() + ",";
            } else {
                csv += ",,";
            }
            if (i < samples.get(key).to.size()) {
                csv += " " + samples.get(key).to_time.get(i).toString() + ", ";
                csv += samples.get(key).to.get(i).toString() + "\n";
            } else {
                csv += ",\n";
            }
        }
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, csv);
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }

    //creates an png of the plot and share it
    private void shareAsImagePressed(View v) {
        v.setDrawingCacheEnabled(true);
        Bitmap icon = v.getDrawingCache();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);
        OutputStream outstream;
        try {
            outstream = getContentResolver().openOutputStream(uri);
            icon.compress(Bitmap.CompressFormat.PNG, 100, outstream);
            outstream.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        share.putExtra(EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share Image"));
    }

    // move to the main activity
    private void switchToMainActivity() {
        Intent switchActivityIntent = new Intent(this, MainActivity.class);
        switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(switchActivityIntent);

    }

    /*
     * we need this because when we press back we don't recreate the activity but we call
     * the old instance of it, that means that the fade effects won't happen therefore
     * so we need to override the onNewIntent to add the effects...
     * I we don't recreate the activity because its a waste of resources
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(R.transition.fade_in_samples, R.transition.fade_out_samples);
    }

    @Override
    public void onBackPressed() {
        switchToMainActivity();
    }

    // adds the scale and shading of the button when it pressed
    public void buttonEffect(View button) {
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.setAlpha((float) 0.75);
                        Animation click_animation = AnimationUtils.loadAnimation(SamplesActivity.this, R.anim.scale_harder);
                        v.startAnimation(click_animation);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.setAlpha((float) 1);
                        v.invalidate();
                        switch (v.getId()) {
                            // handle pressing the trashcan
                            case R.id.trash_im:
                                //generates the pop up of are you sure you want to delete this sample...
                                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            //Yes button clicked
                                            deleteSample(key);
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(SamplesActivity.this);
                                builder.setMessage("Are you sure you want to delete\n" + key + " sample?").setPositiveButton("Yes", dialogClickListener)
                                        .setNegativeButton("No", dialogClickListener).show();
                                break;
                            case R.id.share:
                                //generates the pop up of how would you like to share this sample...
                                DialogInterface.OnClickListener dialogClickListenerShare = (dialog, which) -> {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            //share as image button clicked
                                            shareAsImagePressed(plot);
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //share as text button clicked
                                            try {
                                                shareAsTextPressed();
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                    }
                                };
                                AlertDialog.Builder builderShare = new AlertDialog.Builder(SamplesActivity.this);
                                builderShare.setMessage("How would you like to share \n" + key + " sample?").setPositiveButton("Share as an image", dialogClickListenerShare)
                                        .setNegativeButton("Share as text", dialogClickListenerShare).show();
                                break;

                        }
                        break;
                    }
                }
                return false;
            }
        });
    }

}