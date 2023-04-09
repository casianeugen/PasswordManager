package com.example.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setHomeAsUpIndicator(R.drawable.arrow_back);
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        LinearLayout passwords = findViewById(R.id.passwords);
        LinearLayout payment_cards = findViewById(R.id.payment_cards);

        passwords.setOnClickListener(view -> {
            Intent intent = new Intent(AddMenuActivity.this, AddPasswordActivity.class);
            startActivity(intent);
            finish();
        });
        payment_cards.setOnClickListener(view -> {
            Intent intent = new Intent(AddMenuActivity.this, AddPaymentCardActivity.class);
            startActivity(intent);
            finish();
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
