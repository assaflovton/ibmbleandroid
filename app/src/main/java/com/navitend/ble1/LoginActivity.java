package com.navitend.ble1;

import android.Manifest;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final String tag = "Login activity:  ";
    Animation rotate_animation;
    ImageView logo_iv;
    TextView walk_better_tv;
    private Button login_btn;
    private TextView register_tv;
    private EditText email_et;
    private EditText password_et;
    private ProgressBar progress_bar;
    public static FirebaseAuth mAuth;
    private final int REQUEST_LOCATION_PERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //add the fade in animation
        walk_better_tv = findViewById(R.id.walk_better_tv);
        AlphaAnimation fadeIn = new AlphaAnimation( 0.0f, 1.0f );
        walk_better_tv.startAnimation(fadeIn);
        fadeIn.setDuration(3000);
        fadeIn.setFillAfter(true);
        login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(this);
        // make it smaller when pressed
        buttonEffect(login_btn);
        email_et = findViewById(R.id.email_et);
        password_et = findViewById(R.id.password_et);
        progress_bar = findViewById(R.id.progress_bar);
        logo_iv = findViewById(R.id.logo_iv_login);
        logo_iv.setOnClickListener(this);

        register_tv = findViewById(R.id.register_tv);
        register_tv.setOnClickListener(this);
        buttonEffect(login_btn);
        mAuth = FirebaseAuth.getInstance();
        //make the logo spin after half a second
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        rotateAnimation();
                    }
                },
                500
        );
        //ask for the all the needed permission of the app
        requestLocationPermission();
    }

    //logic of the login, validation with database
    private void userLogin() {

        final String email = email_et.getText().toString().trim();
        final String password = password_et.getText().toString().trim();
        if (email.isEmpty()) {
            email_et.setError("Email is required");
            email_et.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            password_et.setError("Password is required");
            password_et.requestFocus();
            return;
        }
        if (password.length() < 6) {
            password_et.setError("Password need to be at least 6 characters long");
            password_et.requestFocus();
            return;
        }
        progress_bar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                switchToMainActivity();

            } else {
                Toast t = Toast.makeText(LoginActivity.this, "Failed to login: " + task.getException().getMessage(), Toast.LENGTH_LONG);
                t.show();
            }
        });
    }

    //regarding logo spin
    private void rotateAnimation() {
        rotate_animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        logo_iv.startAnimation(rotate_animation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.logo_iv_login:
                rotateAnimation();
                break;
            case R.id.register_tv:
                switchToRegisterActivity();
                break;
        }
    }

    //move to main activity
    private void switchToMainActivity() {
        Intent switchActivityIntent = new Intent(this, MainActivity.class);
        switchActivityIntent.putExtra("email", email_et.getText().toString());
        startActivity(switchActivityIntent);
        overridePendingTransition(R.transition.fade_in_main,R.transition.fade_out);

    }

    // move to register activity
    private void switchToRegisterActivity() {
        Intent switchActivityIntent = new Intent(this, RegisterActivity.class);
        startActivity(switchActivityIntent);
        overridePendingTransition(R.transition.fade_in,R.transition.fade_out);

    }



    @Override
    public void onBackPressed() {//disable the back button from the login activity

    }

    // adds the scale and shading of the button when it pressed
    public void buttonEffect(View button){
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.setAlpha((float)0.93);
                        Animation click_animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.scale);
                        v.startAnimation(click_animation);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.setAlpha((float)1);
                        v.invalidate();
                        switch (v.getId()) {
                            case R.id.login_btn:
                                userLogin();
                                break;

                        }
                        break;
                    }
                }
                return false;
            }
        });
    }

    //Ask for all the needed permissions for the app to run properly
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
//            Toast t = Toast.makeText(this, "Location permission already granted", Toast.LENGTH_SHORT);
//            t.show();
        } else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }
}