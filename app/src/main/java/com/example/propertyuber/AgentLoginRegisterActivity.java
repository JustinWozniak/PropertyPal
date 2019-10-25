package com.example.propertyuber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AgentLoginRegisterActivity extends AppCompatActivity {

    private TextView signUpButton;
    private TextView loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_login_register);

        signUpButton = findViewById(R.id.agentSignup);
        loginButton = findViewById(R.id.agentLogin);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SignUpAgentIntent = new Intent(AgentLoginRegisterActivity.this, AgentSignupActivity.class);
                startActivity(SignUpAgentIntent);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginUpAgentIntent = new Intent(AgentLoginRegisterActivity.this, AgentSigninActivity.class);
                startActivity(LoginUpAgentIntent);
            }
        });
    }
}
