package com.example.propertyuber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AgentSigninActivity extends AppCompatActivity {

    private EditText agentSigninEmail;
    private EditText agentSigninPass1;
    private TextView agentSigninProceedButton;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_signin);

        mAuth = FirebaseAuth.getInstance();

        agentSigninEmail = findViewById(R.id.agentSigninEmail);
        agentSigninPass1 = findViewById(R.id.agentSigninPass1);
        agentSigninProceedButton = findViewById(R.id.agentSigninProceed);

        loadingBar = new ProgressDialog(this);


        agentSigninProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = agentSigninEmail.getText().toString();
                String password = agentSigninPass1.getText().toString();
                signinCustomer(email, password);
            }
        });
    }

    private void signinCustomer(String email, String password) {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(AgentSigninActivity.this, "Please enter an Email Address", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(AgentSigninActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();


            } else {
            loadingBar.setTitle("Agent Sign In...");
            loadingBar.setMessage("Please Wait, We Are Checking Your Credentials...");
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Intent CustomerIntent = new Intent(AgentSigninActivity.this, AgentsMapActivity.class);
                                startActivity(CustomerIntent);
                                Toast.makeText(AgentSigninActivity.this, "Agent Sign In Successful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();



                            } else {
                                Toast.makeText(AgentSigninActivity.this, "Sign In Unsuccessful, Please Check Email/Password..", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }

    }
}
