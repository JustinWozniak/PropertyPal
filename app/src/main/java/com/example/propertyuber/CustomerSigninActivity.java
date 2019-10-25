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

public class CustomerSigninActivity extends AppCompatActivity {

    private EditText customerSigninEmail;
    private EditText customerSigninPass1;
    private EditText customerSigninPass2;
    private TextView customerSigninProceedButton;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_signin);

        mAuth = FirebaseAuth.getInstance();

        customerSigninEmail = findViewById(R.id.customerSigninEmail);
        customerSigninPass1 = findViewById(R.id.customerSigninPass1);
        customerSigninPass2 = findViewById(R.id.customerSigninPass2);
        customerSigninProceedButton = findViewById(R.id.customerSigninProceed);

        loadingBar = new ProgressDialog(this);


        customerSigninProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = customerSigninEmail.getText().toString();
                String password = customerSigninPass1.getText().toString();
                String password2 = customerSigninPass2.getText().toString();
                signinCustomer(email, password, password2);
            }
        });
    }

    private void signinCustomer(String email, String password, String password2) {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(CustomerSigninActivity.this, "Please enter an Email Address", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(CustomerSigninActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();

        }
        if (TextUtils.isEmpty(password2)) {
            Toast.makeText(CustomerSigninActivity.this, "Please enter second password", Toast.LENGTH_SHORT).show();


        } else {
            loadingBar.setTitle("customer SignIn...");
            loadingBar.setMessage("Please wait, we are checking your credentials...");
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Intent CustomerIntent = new Intent(CustomerSigninActivity.this, CustomersMapActivity.class);
                                startActivity(CustomerIntent);
                                Toast.makeText(CustomerSigninActivity.this, "customer Login successful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            } else {
                                Toast.makeText(CustomerSigninActivity.this, "SignIn unsuccessful, please check email/password and try again..", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }

    }
}
