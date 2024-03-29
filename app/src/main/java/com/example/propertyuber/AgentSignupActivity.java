package com.example.propertyuber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AgentSignupActivity extends AppCompatActivity {

    private EditText agentName;
    private EditText agentEmail;
    private EditText agentCar;
    private EditText agentPassword1;
    private EditText agentPassword2;

    private TextView agentSubmitSignup;

    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    private DatabaseReference agentDatabaseRef;

    private String profileImageUrl = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_signup);

        agentName = findViewById(R.id.agentName);
        agentEmail = findViewById(R.id.agentEmail);
        agentCar = findViewById(R.id.agentVehicle);
        agentPassword1 = findViewById(R.id.agentPassword1);
        agentPassword2 = findViewById(R.id.agentPassword2);
        agentSubmitSignup = findViewById(R.id.agentSubmitSignupButton);

        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();


        agentSubmitSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = agentName.getText().toString();
                String email = agentEmail.getText().toString();
                String car = agentCar.getText().toString();
                String pass1 = agentPassword1.getText().toString();
                String pass2 = agentPassword2.getText().toString();
                Boolean isSighnedIn = true;
                String passFinal = pass1;

                if (pass1 != pass2) {
                    passFinal = "NOPE";
                }

                signupCustomer(name, email, car, pass1, pass2, passFinal);
            }
        });
    }


    private void signupCustomer(final String name, final String email, final String car, final String pass1, String pass2, String passFinal) {

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(car)) {
            Toast.makeText(this, "Please enter type of vehicle", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass1)) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass2)) {
            Toast.makeText(this, "Please enter second password", Toast.LENGTH_SHORT).show();
            return;
        }



        else {
            loadingBar.setTitle("Agent Registration...");
            loadingBar.setMessage("Please wait, we are entering your data...");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, pass1)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                String user_id = mAuth.getCurrentUser().getUid();
                                agentDatabaseRef = FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("Users")
                                        .child("Agents")
                                        .child(user_id)
                                        .child("name");
                                agentDatabaseRef.setValue(name);

                                agentDatabaseRef = FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("Users")
                                        .child("Agents")
                                        .child(user_id)
                                        .child("email");
                                agentDatabaseRef.setValue(email);

                                agentDatabaseRef = FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("Users")
                                        .child("Agents")
                                        .child(user_id)
                                        .child("car");
                                agentDatabaseRef.setValue(car);

                                agentDatabaseRef = FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("Users")
                                        .child("Agents")
                                        .child(user_id)
                                        .child("password");
                                agentDatabaseRef.setValue(pass1);

                                agentDatabaseRef = FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("Users")
                                        .child("Agents")
                                        .child(user_id)
                                        .child("profileImageUrl");
                                agentDatabaseRef.setValue(profileImageUrl);

                                Intent AgentIntent = new Intent(AgentSignupActivity.this, AgentsMapActivity.class);
                                startActivity(AgentIntent);

                                Toast.makeText(AgentSignupActivity.this, "Agent registration successful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else {
                                Toast.makeText(AgentSignupActivity.this, "Registration unsuccessful, please try again..", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }


    }
}
