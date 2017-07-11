package com.example.intern05.meetup.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.intern05.meetup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private Button registerB;
    private Button loginB;
    private TextView forgotPassword;

    private TextView email;
    private TextView password;


    private FirebaseAuth mAuth;

    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (TextView) findViewById(R.id.email);
        password = (TextView) findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        forgotPassword = (TextView) findViewById(R.id.forgotPasswordTv);

        pb = (ProgressBar) findViewById(R.id.progBar);
        pb.setVisibility(View.GONE);
        pb.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ForgotPassword.class);
                startActivity(i);
            }
        });

        loginB = (Button) findViewById(R.id.loginB);
        loginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emaill = email.getText().toString().trim();
                String pass = password.getText().toString();
                if (TextUtils.isEmpty(emaill) && TextUtils.isEmpty(pass)) {
                    Toast.makeText(MainActivity.this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
                } else {
                    pb.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                pb.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(MainActivity.this, SlideBarActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
                                pb.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

        registerB = (Button) findViewById(R.id.registerB);
        registerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

    }
}
