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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class AddPasswordActivity extends AppCompatActivity {
    private VaultItemEditorViewModel vaultItemEditorViewModel;
    private VaultItemEntity existingItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vaultItemEditorViewModel = new ViewModelProvider(this).get(VaultItemEditorViewModel.class);
        if (vaultItemEditorViewModel.getCurrentUser() == null) {
            openLogin();
            return;
        }

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
        MaterialCheckBox autofill = findViewById(R.id.autofill);
        MaterialCheckBox notification = findViewById(R.id.notification);
        Button cancel_button = findViewById(R.id.cancel_button);
        Button add_button = findViewById(R.id.add_button);

        Intent intent = getIntent();
        pass_pass.setText(intent.getStringExtra("password"));
        existingItem = vaultItemEditorViewModel.loadItem(intent.getStringExtra("documentId"),
                VaultItemEntity.TYPE_PASSWORD);
        if (existingItem != null) {
            add_button.setText(R.string.edit);
            name_pass.setText(existingItem.name);
            url_pass.setText(existingItem.url);
            username_pass.setText(existingItem.username);
            pass_pass.setText(existingItem.password);
            notes_pass.setText(existingItem.notes);
            autofill.setChecked(existingItem.autofillEnabled);
            notification.setChecked(existingItem.notificationEnabled);
        }

        notes_pass.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                notes_pass.setLines(4);
                notes_pass.setGravity(Gravity.TOP);
            } else {
                notes_pass.setLines(1);
                notes_pass.setGravity(Gravity.CENTER_VERTICAL);
            }
        });

        name_pass.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                name.setErrorEnabled(false);
            }
        });

        cancel_button.setOnClickListener(view -> openMain());
        add_button.setOnClickListener(view -> {
            OperationResult<VaultItemEntity> result = vaultItemEditorViewModel.savePassword(existingItem,
                    Objects.requireNonNull(name_pass.getText()).toString(),
                    Objects.requireNonNull(url_pass.getText()).toString(),
                    Objects.requireNonNull(username_pass.getText()).toString(),
                    Objects.requireNonNull(pass_pass.getText()).toString(),
                    Objects.requireNonNull(notes_pass.getText()).toString(),
                    autofill.isChecked(),
                    notification.isChecked());
            if (!result.isSuccess()) {
                name.setErrorEnabled(true);
                name.setError(result.getMessage());
                return;
            }
            openMain();
        });
    }

    private void openMain() {
        startActivity(new Intent(AddPasswordActivity.this, MainActivity.class));
        finish();
    }

    private void openLogin() {
        startActivity(new Intent(AddPasswordActivity.this, LoginActivity.class));
        finish();
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
