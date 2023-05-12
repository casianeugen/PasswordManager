package com.example.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddPasswordActivity extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db1 = FirebaseFirestore.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setHomeAsUpIndicator(R.drawable.arrow_back);
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        TextInputLayout name = findViewById(R.id.name);

        TextInputEditText notes_pass = findViewById(R.id.notes_pass);
        TextInputEditText name_pass = findViewById(R.id.name_pass);
        TextInputEditText url_pass = findViewById(R.id.url_pass);
        TextInputEditText username_pass = findViewById(R.id.username_pass);
        TextInputEditText pass_pass = findViewById(R.id.pass_pass);
        Button cancel_button = findViewById(R.id.cancel_button);
        Button add_button = findViewById(R.id.add_button);

        Intent i = getIntent();

        pass_pass.setText(i.getStringExtra("password"));
        name_pass.setText(i.getStringExtra("documentId"));
        if(Objects.requireNonNull(name_pass.getText()).toString().equals(i.getStringExtra("documentId"))){
            add_button.setText(R.string.edit);
            DocumentReference pass_edit = db1.collection("users").document(user.getUid())
                    .collection("passwords").document(i.getStringExtra("documentId"));
            pass_edit.get().addOnSuccessListener(documentSnapshot -> {
                if(documentSnapshot.exists()){
                    name_pass.setText(documentSnapshot.getString("3)Name"));
                    url_pass.setText(documentSnapshot.getString("4)Url"));
                    username_pass.setText(documentSnapshot.getString("5)Username"));
                    pass_pass.setText(documentSnapshot.getString("6)Password"));
                    notes_pass.setText(documentSnapshot.getString("7)Notes"));
                }
                else {
                    Log.d("TAG", "No such document");
                }
            });
        }

        notes_pass.setOnFocusChangeListener((view, b) -> {
            if (b) {
                notes_pass.setLines(4);
                notes_pass.setGravity(Gravity.TOP);
            } else {
                notes_pass.setLines(1);
                notes_pass.setGravity(Gravity.CENTER_VERTICAL);
            }
        });

        name_pass.setOnFocusChangeListener((view, b) -> {
            if (b)
                name.setErrorEnabled(false);
        });

        cancel_button.setOnClickListener(view -> {
            Intent intent = new Intent(AddPasswordActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        add_button.setOnClickListener(view -> {
            if (user != null) {
                if (Objects.requireNonNull(name_pass.getText()).toString().isEmpty()) {
                    name.setErrorEnabled(true);
                    name.setError("You must enter a name");
                } else {
                    DocumentReference userDocRef = db1.collection("users").document(user.getUid());
                    CollectionReference passwordColRef = userDocRef.collection("passwords");

                    Map<String, Object> pass_info_map = new HashMap<>();
                    pass_info_map.put("1)Type", "passwords");
                    pass_info_map.put("2)Icon", R.drawable.ic_menu_password);
                    pass_info_map.put("3)Name", name_pass.getText().toString());
                    pass_info_map.put("4)Url", Objects.requireNonNull(url_pass.getText()).toString());
                    pass_info_map.put("5)Username", Objects.requireNonNull(username_pass.getText()).toString());
                    pass_info_map.put("6)Password", Objects.requireNonNull(pass_pass.getText()).toString());
                    pass_info_map.put("7)Notes", Objects.requireNonNull(notes_pass.getText()).toString());
                    if(add_button.getText().toString().equals("Edit"))
                        passwordColRef.document(i.getStringExtra("documentId")).set(pass_info_map);
                    else
                        passwordColRef.document(random_id()).set(pass_info_map);
                    startActivity(new Intent(AddPasswordActivity.this, MainActivity.class));
                    finish();
                }
            } else {
                Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public String random_id(){
        SecureRandom random = new SecureRandom();
        return String.valueOf(random.nextInt(1000));
    }
}
