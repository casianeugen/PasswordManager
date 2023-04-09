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

import java.util.Objects;

public class AddPaymentCardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_card);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setHomeAsUpIndicator(R.drawable.arrow_back);
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        TextInputEditText notes_field = findViewById(R.id.notes_pass);
        Button cancel_button = findViewById(R.id.cancel_button);

        cancel_button.setOnClickListener(view -> {
            Intent intent = new Intent(AddPaymentCardActivity.this, AddMenuActivity.class);
            startActivity(intent);
            finish();
        });

        notes_field.setOnFocusChangeListener((view, b) -> {
            if(b){
                notes_field.setLines(4);
                notes_field.setGravity(Gravity.TOP);
            } else {
                notes_field.setLines(1);
                notes_field.setGravity(Gravity.CENTER_VERTICAL);
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
