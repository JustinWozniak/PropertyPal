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

public class CustomerLoginRegisterActivity extends AppCompatActivity {

    private Button customerLoginButton;
    private Button customerRegisterButton;
    private TextView customerRegisterLink;
    private TextView customerStatusTextView;
    private EditText customerEmailTextField;
    private EditText customerPasswordTextField;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference customerDatabaseRef;
    private String onlineCustomerId;
    private TextView customerDontHaveAccountText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login_register);
        mAuth = FirebaseAuth.getInstance();
        customerLoginButton = findViewById(R.id.customer_login_button);
        customerRegisterButton = findViewById(R.id.customer_register_button);
        customerDontHaveAccountText = findViewById(R.id.customerDontHaveAccountText);
        customerRegisterLink = findViewById(R.id.register_customer_link);
        customerRegisterLink.setGravity(Gravity.CENTER);
        customerStatusTextView = findViewById(R.id.customer_status);
        customerStatusTextView.setGravity(Gravity.CENTER);
        customerRegisterButton.setVisibility(View.INVISIBLE);
        customerRegisterButton.setEnabled(false);
        customerEmailTextField = findViewById(R.id.customer_email);
        customerPasswordTextField = findViewById((R.id.customer_password));

        loadingBar = new ProgressDialog(this);


        customerRegisterLink.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                customerLoginButton.setVisibility(View.INVISIBLE);
                customerRegisterLink.setVisibility(View.INVISIBLE);
                customerStatusTextView.setGravity(Gravity.CENTER);
                customerStatusTextView.setText("Register");
                customerDontHaveAccountText.setVisibility(View.INVISIBLE);

                customerRegisterButton.setVisibility(View.VISIBLE);
                customerRegisterButton.setEnabled(true);
            }
        });
        customerRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = customerEmailTextField.getText().toString();
                String password = customerPasswordTextField.getText().toString();

                registerCustomer(email, password);

            }
        });

        customerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = customerEmailTextField.getText().toString();
                String password = customerPasswordTextField.getText().toString();
                signinCustomer(email, password);
            }
        });

    }

    private void signinCustomer(String email, String password) {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(CustomerLoginRegisterActivity.this, "Please enter an Email Address", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(CustomerLoginRegisterActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Customer Login...");
            loadingBar.setMessage("Please wait, we are checking your credentials...");
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Intent CustomerIntent = new Intent(CustomerLoginRegisterActivity.this, CustomersMapActivity.class);
                                startActivity(CustomerIntent);
                                Toast.makeText(CustomerLoginRegisterActivity.this, "Customer Login successful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            } else {
                                Toast.makeText(CustomerLoginRegisterActivity.this, "Login unsuccessful, please check email/password and try again..", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }

    }

    private void registerCustomer(String email, String password) {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(CustomerLoginRegisterActivity.this, "Please enter an Email Address", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(CustomerLoginRegisterActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Customer Registration...");
            loadingBar.setMessage("Please wait, we are entering your data...");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                String user_id = mAuth.getCurrentUser().getUid();
                                customerDatabaseRef = FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("Users")
                                        .child("Customers")
                                        .child(user_id)
                                        .child("name");
                                customerDatabaseRef.setValue(user_id);

                                Intent AgentIntent = new Intent(CustomerLoginRegisterActivity.this, CustomersMapActivity.class);
                                startActivity(AgentIntent);

                                Toast.makeText(CustomerLoginRegisterActivity.this, "Customer registration successful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else {
                                Toast.makeText(CustomerLoginRegisterActivity.this, "Registration unsuccessful, please try again..", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }


    }
}
