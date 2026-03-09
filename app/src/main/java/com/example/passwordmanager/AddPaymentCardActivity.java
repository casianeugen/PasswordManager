package com.example.passwordmanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AddPaymentCardActivity extends AppCompatActivity {
    private Calendar calendar;
    private TextInputEditText expiration_payment_card;
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

        setContentView(R.layout.activity_add_payment_card);
        calendar = Calendar.getInstance();

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setHomeAsUpIndicator(R.drawable.arrow_back);
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        TextInputLayout name = findViewById(R.id.name);
        TextInputLayout number = findViewById(R.id.number);

        TextInputEditText name_card = findViewById(R.id.name_card);
        TextInputEditText name_payment_card_edit = findViewById(R.id.name_payment_card_edit);
        TextInputEditText type_payment_card = findViewById(R.id.type_payment_card);
        TextInputEditText number_card = findViewById(R.id.number_card);
        TextInputEditText security_payment_card = findViewById(R.id.security_payment_card);
        TextInputEditText notes_payment_card = findViewById(R.id.notes_payment_card);
        Button add_button = findViewById(R.id.add_button);
        Button cancel_button = findViewById(R.id.cancel_button);
        expiration_payment_card = findViewById(R.id.expiration_payment_card);

        existingItem = vaultItemEditorViewModel.loadItem(getIntent().getStringExtra("documentId"),
                VaultItemEntity.TYPE_PAYMENT_CARD);
        if (existingItem != null) {
            add_button.setText(R.string.edit);
            name_card.setText(existingItem.name);
            name_payment_card_edit.setText(existingItem.cardName);
            type_payment_card.setText(existingItem.cardType);
            number_card.setText(existingItem.number);
            security_payment_card.setText(existingItem.securityCode);
            expiration_payment_card.setText(existingItem.expirationDate);
            notes_payment_card.setText(existingItem.notes);
        }

        add_button.setOnClickListener(view -> {
            OperationResult<VaultItemEntity> result = vaultItemEditorViewModel.savePaymentCard(existingItem,
                    Objects.requireNonNull(name_card.getText()).toString(),
                    Objects.requireNonNull(name_payment_card_edit.getText()).toString(),
                    Objects.requireNonNull(type_payment_card.getText()).toString(),
                    Objects.requireNonNull(number_card.getText()).toString(),
                    Objects.requireNonNull(security_payment_card.getText()).toString(),
                    Objects.requireNonNull(expiration_payment_card.getText()).toString(),
                    Objects.requireNonNull(notes_payment_card.getText()).toString());
            if (!result.isSuccess()) {
                if ("Enter a valid card number".equals(result.getMessage())) {
                    number.setErrorEnabled(true);
                    number.setError(result.getMessage());
                    return;
                }
                name.setErrorEnabled(true);
                name.setError(result.getMessage());
                return;
            }
            openMain();
        });

        cancel_button.setOnClickListener(view -> openMain());
        name_card.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                name.setErrorEnabled(false);
            }
        });

        expiration_payment_card.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddPaymentCardActivity.this, dateSetListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        notes_payment_card.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                notes_payment_card.setLines(4);
                notes_payment_card.setGravity(Gravity.TOP);
            } else {
                notes_payment_card.setLines(1);
                notes_payment_card.setGravity(Gravity.CENTER_VERTICAL);
            }
        });
    }

    private final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            updateMonthYearEditText();
            expiration_payment_card.clearFocus();
        }
    };

    private void updateMonthYearEditText() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        expiration_payment_card.setText(simpleDateFormat.format(calendar.getTime()));
    }

    private void openMain() {
        startActivity(new Intent(AddPaymentCardActivity.this, MainActivity.class));
        finish();
    }

    private void openLogin() {
        startActivity(new Intent(AddPaymentCardActivity.this, LoginActivity.class));
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
