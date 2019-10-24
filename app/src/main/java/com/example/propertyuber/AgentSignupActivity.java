package com.example.propertyuber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
    private EditText agentPhoneNumber;
    private EditText agentPassword1;
    private EditText agentPassword2;

    private Button agentSubmitSignupButton;

    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    private DatabaseReference agentDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_signup);

        agentName = findViewById(R.id.agentName);
        agentEmail = findViewById(R.id.agentEmail);
        agentCar = findViewById(R.id.agentVehicle);
        agentPhoneNumber = findViewById(R.id.agentPhone);
        agentPassword1 = findViewById(R.id.agentPassword1);
        agentPassword2 = findViewById(R.id.agentPassword2);
        agentSubmitSignupButton = findViewById(R.id.agentSubmitSignupButton);

        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();


        agentSubmitSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = agentName.getText().toString();
                String email = agentEmail.getText().toString();
                String car = agentCar.getText().toString();
                String phone = agentPhoneNumber.getText().toString();
                String pass1 = agentPassword1.getText().toString();
                String pass2 = agentPassword2.getText().toString();

                signupCustomer(name, email, car, phone, pass1, pass2);
            }
        });
    }


    private void signupCustomer(final String name, final String email, final String car, final String phone, final String pass1, String pass2) {

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
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
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
                                        .child("phone");
                                agentDatabaseRef.setValue(phone);

                                agentDatabaseRef = FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("Users")
                                        .child("Agents")
                                        .child(user_id)
                                        .child("password");
                                agentDatabaseRef.setValue(pass1);

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
