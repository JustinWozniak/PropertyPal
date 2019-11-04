package com.example.propertyuber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class agentsProfileActivity extends AppCompatActivity {


    private TextView agentsNameField;
    private TextView agentsCar;

    private Button mBack;

    private ImageView agentProfileImage;
    private String agentFoundID = "";
    private FirebaseAuth mAuth;
    private DatabaseReference mAgentsDatabaseRef;

    private String userID;
    private String mName;
    private String mProfileImageUrl;

    private Uri resultUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agents_profile);

        agentsNameField = findViewById(R.id.agentsName);
        agentsCar = findViewById(R.id.agentsCar);
        agentProfileImage =  findViewById(R.id.agentProfileImage);

        mBack =  findViewById(R.id.agentProfileBack);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mAgentsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Agents");
        Log.e("AGENTSNAME", mAgentsDatabaseRef.toString());
        getUserInfo();

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });
    }
    private void getUserInfo() {
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Agents").child(agentFoundID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    if (dataSnapshot.child("name") != null) {
//                        Log.e("jnhgjjgh",dataSnapshot.child("name").getValue().toString());
//                        agentsNameField.setText(dataSnapshot.child("name").getValue().toString());
                    }
//                    if (dataSnapshot.child("car") != null) {
//                        agentsCar.setText(dataSnapshot.child("car").getValue().toString());
//                    }
//                    if (dataSnapshot.child("profileImageUrl") != null) {
//                        Glide.with(getApplication()).load(dataSnapshot.child("profileImageUrl").getValue().toString()).into(agentProfileImage);
//                    }

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }}