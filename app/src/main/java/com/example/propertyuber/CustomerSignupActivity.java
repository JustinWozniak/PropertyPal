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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerSignupActivity extends AppCompatActivity {

    private EditText customerName;
    private EditText customerEmail;
    private EditText customerCar;
    private EditText customerPassword1;
    private EditText customerPassword2;

    private TextView customerSubmitSignup;

    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    private DatabaseReference customerDatabaseRef;

    private String profileImageUrl = "";
    Boolean isSighnedIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_signup);

        customerName = findViewById(R.id.customerName);
        customerEmail = findViewById(R.id.customerEmail);
        customerCar = findViewById(R.id.customerVehicle);
        customerPassword1 = findViewById(R.id.customerPassword1);
        customerPassword2 = findViewById(R.id.customerPassword2);
        customerSubmitSignup = findViewById(R.id.customerSubmitSignupButton);

        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();


        customerSubmitSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = customerName.getText().toString();
                String email = customerEmail.getText().toString();
                String car = customerCar.getText().toString();
                String pass1 = customerPassword1.getText().toString();
                String pass2 = customerPassword2.getText().toString();

                signupCustomer(name, email, car, pass1, pass2);
            }
        });
    }


    private void signupCustomer(final String name, final String email, final String car, final String pass1, String pass2) {

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
            loadingBar.setTitle("customer Registration...");
            loadingBar.setMessage("Please wait, we are entering your data...");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, pass1)
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
                                customerDatabaseRef.setValue(name);

                                customerDatabaseRef = FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("Users")
                                        .child("Customers")
                                        .child(user_id)
                                        .child("email");
                                customerDatabaseRef.setValue(email);

                                customerDatabaseRef = FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("Users")
                                        .child("Customers")
                                        .child(user_id)
                                        .child("car");
                                customerDatabaseRef.setValue(car);

                                customerDatabaseRef = FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("Users")
                                        .child("Customers")
                                        .child(user_id)
                                        .child("password");
                                customerDatabaseRef.setValue(pass1);

                                customerDatabaseRef = FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("Users")
                                        .child("Customers")
                                        .child(user_id)
                                        .child("profileImageUrl");
                                customerDatabaseRef.setValue(profileImageUrl);

                                customerDatabaseRef = FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("Users")
                                        .child("Customers")
                                        .child(user_id)
                                        .child("signedIn");
                                customerDatabaseRef.setValue(isSighnedIn);

                                Intent customerIntent = new Intent(CustomerSignupActivity.this, CustomersMapActivity.class);
                                startActivity(customerIntent);

                                Toast.makeText(CustomerSignupActivity.this, "customer registration successful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else {
                                Toast.makeText(CustomerSignupActivity.this, "Registration unsuccessful, please try again..", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }


    }
}
