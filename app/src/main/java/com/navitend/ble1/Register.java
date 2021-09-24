package com.navitend.ble1;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
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
import com.google.firebase.ktx.Firebase;


public class Register extends AppCompatActivity implements View.OnClickListener {
    private Animation rotate_animation;
    private ImageView logo_iv;
    private final String tag = "We said that: ";
    private FirebaseAuth mAuth;
    private Button register_btn;
    private EditText name_et;
    private EditText email_et;
    private EditText password_et;
    private ProgressBar progress_bar;
    private TextView home_tv;
    private boolean reg_suc = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // database object
        mAuth = FirebaseAuth.getInstance();
        register_btn = findViewById(R.id.register_btn);
        register_btn.setOnClickListener(this);
        name_et = findViewById(R.id.name_reg_et);
        email_et = findViewById(R.id.email_reg_et);
        password_et = findViewById(R.id.password_reg_et);
        progress_bar = findViewById(R.id.progress_bar_reg);
        home_tv = findViewById(R.id.home_reg_tv);
        home_tv.setOnClickListener(this);
        logo_iv = findViewById(R.id.logo_iv_reg);
        logo_iv.setOnClickListener(this);
        //logo spin
        rotateAnimation();


    }

    // take the input from the activity and creates a new user in the database
    private void registerUser() {
        //need to do saving
        final String name = name_et.getText().toString().trim();
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
                    FirebaseDatabase.getInstance().getReference().child("Users").push().setValue(user);
                    switchToMainActivity(email);

                } else {
                    Toast t = Toast.makeText(Register.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG);
                    t.show();
                    progress_bar.setVisibility(View.GONE);
                }
            }
        });



    }

    // move between activies
    private void switchToLoginActivity() {
        //need to do saving
        Intent switchActivityIntent = new Intent(this, LoginActivity.class);
        startActivity(switchActivityIntent);
    }

    private void switchToMainActivity(String email) {
        Intent switchActivityIntent = new Intent(this, MainActivity.class);
        switchActivityIntent.putExtra("email", email);
        startActivity(switchActivityIntent);
    }

    // reguarding view
    private void rotateAnimation() {

        rotate_animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        logo_iv.startAnimation(rotate_animation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.register_btn:
                registerUser();
                break;
            case R.id.home_reg_tv:
                switchToLoginActivity();
                break;
            case R.id.logo_iv_reg:
                rotateAnimation();
                break;
        }

    }
}