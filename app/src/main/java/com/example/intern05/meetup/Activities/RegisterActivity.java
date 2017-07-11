package com.example.intern05.meetup.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.intern05.meetup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email, pwd, username;
    private Button registerB;

    private ProgressBar progBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        email = (EditText) findViewById(R.id.emailR);
        pwd = (EditText) findViewById(R.id.pwd);
        username = (EditText) findViewById(R.id.userName);

        mAuth = FirebaseAuth.getInstance();


        progBar = (ProgressBar) findViewById(R.id.progBar);
        progBar.setVisibility(View.GONE);
        progBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        registerB = (Button) findViewById(R.id.registerB);
        registerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emaill = email.getText().toString().trim();
                String pass = pwd.getText().toString().trim();
                String user = username.getText().toString().trim();
                if (TextUtils.isEmpty(emaill) && TextUtils.isEmpty(pass) && TextUtils.isEmpty(user)) {
                    Toast.makeText(RegisterActivity.this, "Fields cannot be empty.", Toast.LENGTH_SHORT).show();
                } else {
                    progBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), pwd.getText().toString()).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                try {
                                    Toast.makeText(RegisterActivity.this, "User registered succesfully", Toast.LENGTH_SHORT).show();

                                    DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("Username", username.getText().toString());
                                    map.put("Email", email.getText().toString());
                                    map.put("ImageUri","null");
                                    map.put("Gender","null");
                                    map.put("BirthDate","");
                                    root.setValue(map);


                                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);

                                    startActivity(i);
                                    progBar.setVisibility(View.GONE);
                                } catch (Exception ex) {
                                    Toast.makeText(RegisterActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
                                progBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });


    }
}
