package com.ekart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ekart.shopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.regex.Pattern;

public class AdminRegisterActivity extends AppCompatActivity {
    private Button CreateAdminAccountButton;
    private EditText AdminName, AdminPhoneNumber, AdminPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        CreateAdminAccountButton = findViewById(R.id.admin_register_btn);
        AdminName = findViewById(R.id.register_username_input);
        AdminPassword = findViewById(R.id.register_password_input);
        AdminPhoneNumber = findViewById(R.id.register_phone_number_input);
        loadingBar = new ProgressDialog(this);

        CreateAdminAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAdminAccount();
            }
        });
    }

    private void CreateAdminAccount() {
        String name = AdminName.getText().toString();
        String phone = AdminPhoneNumber.getText().toString();
        String password = AdminPassword.getText().toString();

        if (!phone.matches("\\d{10}")) {
            Toast.makeText(this, "Invalid phone number...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 7) {
            Toast.makeText(this, "Password length should be at least 7 characters...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please write the admin's name...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please write the admin's phone number...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please write the admin's password...", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Create Admin Account");
            loadingBar.setMessage("Please wait while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateAdminPhoneNumber(name, phone, password);
        }
    }

    private void ValidateAdminPhoneNumber(final String name, final String phone, final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("Admins").child(phone).exists()) {
                    HashMap<String, Object> adminDataMap = new HashMap<>();
                    adminDataMap.put("phone", phone);
                    adminDataMap.put("password", password);
                    adminDataMap.put("name", name);
                    RootRef.child("Admins").child(phone).updateChildren(adminDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AdminRegisterActivity.this, "Congratulations, admin account has been created.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent intent = new Intent(AdminRegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(AdminRegisterActivity.this, "Network Error: Please try again after some time...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(AdminRegisterActivity.this, "This " + phone + " already exists as an admin.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(AdminRegisterActivity.this, "Please try again using another phone number.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminRegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
