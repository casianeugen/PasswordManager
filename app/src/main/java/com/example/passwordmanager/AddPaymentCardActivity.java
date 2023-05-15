package com.example.passwordmanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AddPaymentCardActivity extends AppCompatActivity {
    private Calendar calendar;
    TextInputEditText expiration_payment_card;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db1 = FirebaseFirestore.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_card);

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

        Intent i = getIntent();
        name_card.setText(i.getStringExtra("documentId"));
        if(Objects.requireNonNull(name_card.getText()).toString().equals(i.getStringExtra("documentId"))){
            add_button.setText(R.string.edit);
            DocumentReference pass_edit = db1.collection("users").document(user.getUid())
                    .collection("payment_card").document(i.getStringExtra("documentId"));
            pass_edit.get().addOnSuccessListener(documentSnapshot -> {
                if(documentSnapshot.exists()){
                    name_card.setText(documentSnapshot.getString("3)Name"));
                    name_payment_card_edit.setText(documentSnapshot.getString("4)Card Name"));
                    type_payment_card.setText(documentSnapshot.getString("5)Card Type"));
                    number_card.setText(documentSnapshot.getString("6)Number"));
                    security_payment_card.setText(documentSnapshot.getString("7)Security Code"));
                    expiration_payment_card.setText(documentSnapshot.getString("8)Expiration Date"));
                    notes_payment_card.setText(documentSnapshot.getString("9)Notes"));
                }
                else {
                    Log.d("TAG", "No such document");
                }
            });
        }

        add_button.setOnClickListener(view -> {
            if (user != null) {
                if (!isPaymentCardNumberValid(Objects.requireNonNull(number_card.getText()).toString())){
                    number.setErrorEnabled(true);
                    number.setError("Enter a valid card number");
                } else if (Objects.requireNonNull(name_card.getText()).toString().isEmpty()) {
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
                    card_info_map.put("9)Notes", Objects.requireNonNull(notes_payment_card.getText()).toString());
                    card_info_map.put("10)UpdatedTime", formatter.format(date));
                    if(add_button.getText().toString().equals("Edit"))
                        passwordColRef.document(i.getStringExtra("documentId")).set(card_info_map);
                    else
                        passwordColRef.document(random_id()).set(card_info_map);
                    startActivity(new Intent(AddPaymentCardActivity.this, MainActivity.class));
                    finish();
                }
            } else {
                Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
            }
        });

        cancel_button.setOnClickListener(view -> {
            Intent intent = new Intent(AddPaymentCardActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        name_card.setOnFocusChangeListener((view, b) -> {
            if (b)
                name.setErrorEnabled(false);
        });

        expiration_payment_card.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddPaymentCardActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        calendar = Calendar.getInstance();

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

    public boolean isPaymentCardNumberValid(String cardNumber) {
        String cardPattern = "^(?:(4\\d{12}(?:\\d{3})?)|(5[1-5]\\d{14})|(3[47]\\d{13})|(3(?:0[0-5]|[68]\\d)\\d{11})|(6(?:011|5\\d{2})\\d{12})|((?:2131|1800|35\\d{3})\\d{11}))$";
        if (!cardNumber.matches(cardPattern)) {
            return false;
        }

        // Get the first digit of the card number to determine the card type
        int firstDigit = Integer.parseInt(cardNumber.substring(0, 1));

        // Check the length of the card number based on the card type
        if (firstDigit == 4 && (cardNumber.length() == 13 || cardNumber.length() == 16)) {
            return true; // Visa
        } else if (firstDigit == 5 && cardNumber.length() == 16) {
            return true; // Mastercard
        } else if ((firstDigit == 3 && (cardNumber.startsWith("34") || cardNumber.startsWith("37"))) && cardNumber.length() == 15) {
            return true; // American Express
        } else return ((firstDigit == 6 && (cardNumber.startsWith("6011") || (cardNumber.substring(0, 3).compareTo("622") >= 0 && cardNumber.substring(0, 3).compareTo("629") <= 0) || cardNumber.startsWith("64") || cardNumber.startsWith("65"))) && cardNumber.length() == 16);
    }

    private final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // Update the calendar with the selected date
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            // Update the EditText with the selected date
            updateMonthYearEditText();
            expiration_payment_card.clearFocus();
        }
    };

    private void updateMonthYearEditText() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        expiration_payment_card.setText(simpleDateFormat.format(calendar.getTime()));
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
