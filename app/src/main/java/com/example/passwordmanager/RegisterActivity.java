package com.example.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private TextInputLayout email_text;
    private TextInputEditText email_edit;
    private MaterialCheckBox terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        email_text = findViewById(R.id.reg_mail);
        email_edit = findViewById(R.id.reg_email);
        terms = findViewById(R.id.PassTerms);
        TextView reg_link_to_log = findViewById(R.id.log_in_reg);
        reg_link_to_log.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        Button reg_continue = findViewById(R.id.button_email_to_pass);
        reg_continue.setOnClickListener(v -> {
            try {
                if(terms.isChecked()){
                    auth.fetchSignInMethodsForEmail(Objects.requireNonNull(email_edit.getText()).toString())
                            .addOnCompleteListener(task -> {
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    email_text.setErrorEnabled(true);
                                    email_text.setError("Enter a valid email address");
                                    email_text.setDefaultHintTextColor(ColorStateList.valueOf(Color.RED));
                                } else {
                                    boolean check = !Objects.requireNonNull(task.getResult().getSignInMethods()).isEmpty();
                                    if (!check) {
                                        Intent intent = new Intent(RegisterActivity.this, RegisterActivityPass.class);
                                        intent.putExtra("email", email_edit.getText().toString());
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        email_text.setErrorEnabled(true);
                                        email_text.setError("Email already exist");
                                        email_text.setDefaultHintTextColor(ColorStateList.valueOf(Color.RED));
                                    }
                                }
                            });
                } else {
                    terms.setError("Please check this box");
                    terms.setTextColor(ColorStateList.valueOf(Color.RED));
                }

            } catch (IllegalArgumentException ex) {
                email_text.setErrorEnabled(true);
                email_text.setError("Field is empty");
                email_text.setDefaultHintTextColor(ColorStateList.valueOf(Color.RED));
            }
        });
    }
}
