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

public class RegisterActivity extends AppCompatActivity {
    private Button createAccountButton;
    private Button adminRegisterButton;
    private EditText inputName, inputPhoneNumber, inputPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createAccountButton = findViewById(R.id.register_btn);
        adminRegisterButton = findViewById(R.id.admin_register_btn);
        inputName = findViewById(R.id.register_username_input);
        inputPassword = findViewById(R.id.register_password_input);
        inputPhoneNumber = findViewById(R.id.register_phone_number_input);
        loadingBar = new ProgressDialog(this);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

        adminRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                Intent intent = new Intent(RegisterActivity.this, AdminRegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void createAccount() {
        String name = inputName.getText().toString();
        String phone = inputPhoneNumber.getText().toString();
        String password = inputPassword.getText().toString();

        if (!phone.matches("\\d{10}")) {
            Toast.makeText(this, "Invalid phone number...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 7) {
            Toast.makeText(this, "Password length should be at least 7 characters...",Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all the required fields...", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            validatePhoneNumber(name, phone, password);
        }
    }

    private void validatePhoneNumber(final String name, final String phone, final String password) {
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("Users").child(phone).exists()) {
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("phone", phone);
                    userDataMap.put("password", password);
                    userDataMap.put("name", name);

                    rootRef.child("Users").child(phone).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Congratulations, your account has been created.", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                loadingBar.dismiss();
                                Toast.makeText(RegisterActivity.this, "Network Error: Please try again after some time...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "This " + phone + " already exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please try again using another phone number.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
