package com.example.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddPaymentCardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_card);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setHomeAsUpIndicator(R.drawable.arrow_back);
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        TextInputLayout name = findViewById(R.id.name);

        TextInputEditText name_card = findViewById(R.id.name_card);
        TextInputEditText name_payment_card_edit = findViewById(R.id.name_payment_card_edit);
        TextInputEditText type_payment_card = findViewById(R.id.type_payment_card);
        TextInputEditText number_card = findViewById(R.id.number_card);
        TextInputEditText security_payment_card = findViewById(R.id.security_payment_card);
        TextInputEditText expiration_payment_card = findViewById(R.id.expiration_payment_card);
        TextInputEditText notes_payment_card = findViewById(R.id.notes_payment_card);
        Button add_button = findViewById(R.id.add_button);
        Button cancel_button = findViewById(R.id.cancel_button);

        add_button.setOnClickListener(view -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseFirestore db1 = FirebaseFirestore.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                if (Objects.requireNonNull(name_card.getText()).toString().isEmpty()) {
                    name.setErrorEnabled(true);
                    name.setError("You must enter a name");
                } else {
                    DocumentReference userDocRef = db1.collection("users").document(user.getUid());
                    CollectionReference passwordColRef = userDocRef.collection("payment_card");

                    Map<String, Object> card_info_map = new HashMap<>();
                    card_info_map.put("1)Type", "payment_card");
                    card_info_map.put("2)Icon", R.drawable.ic_menu_card);
                    card_info_map.put("3)Name", name_card.getText().toString());
                    card_info_map.put("4)Card Name", Objects.requireNonNull(name_payment_card_edit.getText()).toString());
                    card_info_map.put("5)Card Type", Objects.requireNonNull(type_payment_card.getText()).toString());
                    card_info_map.put("6)Number", Objects.requireNonNull(number_card.getText()).toString());
                    card_info_map.put("7)Security Code", Objects.requireNonNull(security_payment_card.getText()).toString());
                    card_info_map.put("8)Expiration Date", Objects.requireNonNull(expiration_payment_card.getText()).toString());
                    passwordColRef.document(name_card.getText().toString()).set(card_info_map);
                    startActivity(new Intent(AddPaymentCardActivity.this, MainActivity.class));
                }
            } else {
                Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
            }
        });


        cancel_button.setOnClickListener(view -> {
            Intent intent = new Intent(AddPaymentCardActivity.this, AddMenuActivity.class);
            startActivity(intent);
            finish();
        });

        name_card.setOnFocusChangeListener((view, b) -> {
            if (b)
                name.setErrorEnabled(false);
        });

        notes_payment_card.setOnFocusChangeListener((view, b) -> {
            if(b){
                notes_payment_card.setLines(4);
                notes_payment_card.setGravity(Gravity.TOP);
            } else {
                notes_payment_card.setLines(1);
                notes_payment_card.setGravity(Gravity.CENTER_VERTICAL);
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
