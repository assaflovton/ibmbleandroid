package com.navitend.ble1;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Animation rotate_animation;
    private ImageView logo_iv;
    private final String tag = "Register Activity: ";
    private FirebaseAuth mAuth;
    private Button register_btn;
    TextView walk_better_tv;
    private EditText name_et;
    private EditText email_et;
    private EditText password_et;
    private ProgressBar progress_bar;
    private TextView home_tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // database object
        mAuth = FirebaseAuth.getInstance();
        // ui objects
        register_btn = findViewById(R.id.register_btn);
        register_btn.setOnClickListener(this);
        buttonEffect(register_btn);
        //add the fade in animation
        walk_better_tv = findViewById(R.id.walk_better_register_tv);
        AlphaAnimation fadeIn = new AlphaAnimation( 0.0f, 1.0f );
        walk_better_tv.startAnimation(fadeIn);
        fadeIn.setDuration(3000);
        fadeIn.setFillAfter(true);
        name_et = findViewById(R.id.name_reg_et);
        email_et = findViewById(R.id.email_reg_et);
        password_et = findViewById(R.id.password_reg_et);
        progress_bar = findViewById(R.id.progress_bar_reg);
        home_tv = findViewById(R.id.home_reg_tv);
        home_tv.setOnClickListener(this);
        logo_iv = findViewById(R.id.logo_iv_reg);
        logo_iv.setOnClickListener(this);
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
    }

    // take the input from the activity and creates a new user in the database
    private void registerUser() {
        final String name = name_et.getText().toString().trim(); // trim remove spaces at ends
        final String email = email_et.getText().toString().trim();
        String password = password_et.getText().toString().trim();
        if (name.isEmpty()) {
            name_et.setError("Name is required");
            name_et.requestFocus();
            return;
        }
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

        Log.i(tag, "manage to collect name: " + name + " Email: " + email + " password: " + password);
        progress_bar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final User user = new User(name, email);
                    //write the data to the database
                    FirebaseDatabase.getInstance().getReference().child("Users").push().setValue(user);
                    switchToMainActivity(email);//finished registration move to the main activity
                } else { //print message to the user with appropriate explanation of the problem
                    Toast t = Toast.makeText(RegisterActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG);
                    t.show();
                    progress_bar.setVisibility(View.GONE);
                }
            }
        });
    }

    // move back to the login activity
    private void switchToLoginActivity() {
        Intent switchActivityIntent = new Intent(this, LoginActivity.class);
        startActivity(switchActivityIntent);
        overridePendingTransition(R.transition.fade_in_main,R.transition.fade_out);
    }

    // move to the main activity
    private void switchToMainActivity(String email) {
        Intent switchActivityIntent = new Intent(this, MainActivity.class);
        switchActivityIntent.putExtra("email", email);
        startActivity(switchActivityIntent);
        overridePendingTransition(R.transition.fade_in_main,R.transition.fade_out);
    }

    public void onBackPressed() {
        switchToLoginActivity();
    }

    // regarding logo spin
    private void rotateAnimation() {
        rotate_animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        logo_iv.startAnimation(rotate_animation);
    }

    @Override
    // handle clicking the register button, the home button and the logo
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.home_reg_tv:
                switchToLoginActivity();
                break;
            case R.id.logo_iv_reg:
                rotateAnimation();
                break;
        }

    }

    // adds the scale and shading of the button when it pressed
    public void buttonEffect(View button){
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //v.getBackground().setAlpha(240);//make the shadow effect
                        v.setAlpha((float)0.93);
                        Animation click_animation = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.scale);
                        v.startAnimation(click_animation);

                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.setAlpha((float)1);
                        v.invalidate();
                        switch (v.getId()) {
                            case R.id.register_btn:
                                Log.i("heyyyyy", "WTF");
                                registerUser();
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