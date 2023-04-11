package com.example.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddPasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setHomeAsUpIndicator(R.drawable.arrow_back);
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        TextInputLayout name = findViewById(R.id.name);
        TextInputLayout url = findViewById(R.id.url);
        TextInputLayout username = findViewById(R.id.username);
        TextInputLayout password = findViewById(R.id.password);
        TextInputLayout notes = findViewById(R.id.notes);

        TextInputEditText notes_pass = findViewById(R.id.notes_pass);
        TextInputEditText name_pass = findViewById(R.id.name_pass);
        TextInputEditText url_pass = findViewById(R.id.url_pass);
        TextInputEditText username_pass = findViewById(R.id.username_pass);
        TextInputEditText pass_pass = findViewById(R.id.pass_pass);
        Button cancel_button = findViewById(R.id.cancel_button);
        Button add_button = findViewById(R.id.add_button);

        Intent i = getIntent();

        pass_pass.setText(i.getStringExtra("password"));

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
            Intent intent = new Intent(AddPasswordActivity.this, AddMenuActivity.class);
            startActivity(intent);
            finish();
        });

        add_button.setOnClickListener(view -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseFirestore db1 = FirebaseFirestore.getInstance();

            DocumentReference userDocRef = db1.collection("users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
            DocumentReference passwordDocRef = db1.collection("passwords").document();

            Map<String, Object> updates = new HashMap<>();
            updates.put("password_id", passwordDocRef.getId());

            userDocRef.update(updates);

            if (Objects.requireNonNull(name_pass.getText()).toString().isEmpty()) {
                name.setErrorEnabled(true);
                name.setError("You must enter a name");
            } else {
                Map<String, Object> pass_info_map = new HashMap<>();
                pass_info_map.put("Type", "password");
                pass_info_map.put("Icon", R.drawable.ic_menu_password);
                pass_info_map.put("Name", name_pass.getText().toString());
                pass_info_map.put("Url", Objects.requireNonNull(url_pass.getText()).toString());
                pass_info_map.put("Username", Objects.requireNonNull(username_pass.getText()).toString());
                pass_info_map.put("Password", Objects.requireNonNull(pass_pass.getText()).toString());
                pass_info_map.put("Notes", Objects.requireNonNull(notes_pass.getText()).toString());
                passwordDocRef.set(pass_info_map);
                startActivity(new Intent(AddPasswordActivity.this, MainActivity.class));
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
}
