package com.example.propertyuber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    private TextView welcome_agent_button;
    private TextView welcome_houses_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welcome_houses_button = findViewById(R.id.welcome_houses_btn);
        welcome_agent_button = findViewById(R.id.welcome_agent_btn);

        welcome_houses_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginRegisterHouseIntent = new Intent(WelcomeActivity.this, CustomerLoginRegisterActivity.class);
                startActivity(LoginRegisterHouseIntent);
            }
        });

        welcome_agent_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginRegisterAgentIntent = new Intent(WelcomeActivity.this, AgentLoginRegisterActivity.class);
                startActivity(LoginRegisterAgentIntent);
            }
        });

    }
}
