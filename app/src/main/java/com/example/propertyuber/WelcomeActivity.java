package com.example.propertyuber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    private Button welcome_agent_button;
    private Button welcome_houses_button;


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
