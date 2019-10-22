package com.example.propertyuber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
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

public class AgentLoginRegisterActivity extends AppCompatActivity {

    private Button agentLoginButton;
    private Button agentRegisterButton;
    private TextView agentRegisterLink;
    private TextView agentStatusTextView;
    private EditText agentEmailTextField;
    private EditText agentPasswordTextField;
    private TextView dontHaveAccountText;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference agentDatabaseRef;
    private String onlineAgentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_login_register);
        mAuth = FirebaseAuth.getInstance();
        agentLoginButton = findViewById(R.id.agent_login_button);
        agentRegisterButton = findViewById(R.id.agent_register_button);
        agentRegisterLink = findViewById(R.id.agent_register_link);
        dontHaveAccountText = findViewById(R.id.dontHaveAccountText);
        agentRegisterLink.setGravity(Gravity.CENTER);
        agentStatusTextView = findViewById(R.id.agent_status);
//        agentStatusTextView.setGravity(Gravity.CENTER);
        agentRegisterButton.setVisibility(View.INVISIBLE);
        agentRegisterButton.setEnabled(false);
        agentEmailTextField = findViewById(R.id.agent_email);
        agentPasswordTextField = findViewById((R.id.agent_password));
        loadingBar = new ProgressDialog(this);

        agentRegisterLink.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                agentLoginButton.setVisibility(View.INVISIBLE);
                agentRegisterLink.setVisibility(View.INVISIBLE);
                agentStatusTextView.setGravity(Gravity.CENTER);
                agentStatusTextView.setText("Register");
                dontHaveAccountText.setVisibility(View.INVISIBLE);

                agentRegisterButton.setVisibility(View.VISIBLE);
                agentRegisterButton.setEnabled(true);
            }
        });
        agentLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = agentEmailTextField.getText().toString();
                String password = agentPasswordTextField.getText().toString();

                signInAgent(email, password);
            }
        });

        agentRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = agentEmailTextField.getText().toString();
                String password = agentPasswordTextField.getText().toString();
                registerAgent(email, password);
            }
        });


    }

    private void signInAgent(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(AgentLoginRegisterActivity.this, "Please enter an Email Address", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(AgentLoginRegisterActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Agent Login");
            loadingBar.setMessage("Please wait, while we are checking your credentials...");
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent AgentIntent = new Intent(AgentLoginRegisterActivity.this, AgentsMapActivity.class);
                                startActivity(AgentIntent);

                                Toast.makeText(AgentLoginRegisterActivity.this, "Agent login successful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else {
                                Toast.makeText(AgentLoginRegisterActivity.this, "Login unsuccessful, please check email/password and try again...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void registerAgent(String email, String password) {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(AgentLoginRegisterActivity.this, "Please enter an Email Address", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(AgentLoginRegisterActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Agent Registration");
            loadingBar.setMessage("Please wait, we are entering your data...");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
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

                                agentDatabaseRef.setValue(user_id);

                                Intent AgentIntent = new Intent(AgentLoginRegisterActivity.this, AgentsMapActivity.class);
                                startActivity(AgentIntent);

                                Toast.makeText(AgentLoginRegisterActivity.this, "Agent registration successful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else {
                                Toast.makeText(AgentLoginRegisterActivity.this, "Registration unsuccessful, please try again..", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

}
