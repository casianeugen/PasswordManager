package com.example.passwordmanager;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout email_text;
    private TextInputEditText email_edit;
    private CheckBox terms;
    private TextView terms_text;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        email_text = findViewById(R.id.reg_mail);
        email_edit = findViewById(R.id.reg_email);
        terms = findViewById(R.id.PassTerms);
        terms_text = findViewById(R.id.terms_text);

        SpannableString s1 = new SpannableString(terms_text.getText().toString());
        s1.setSpan(new ForegroundColorSpan(Color.BLUE), 74, s1.length(), 0);
        s1.setSpan(new UnderlineSpan(), 74, s1.length(), 0);

        terms_text.setText(s1);
        terms_text.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, TermsActivity.class);
            startActivity(intent);
        });

        TextView reg_link_to_log = findViewById(R.id.log_in_reg);
        reg_link_to_log.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        Button reg_continue = findViewById(R.id.button_email_to_pass);
        reg_continue.setOnClickListener(v -> {
            String email = Objects.requireNonNull(email_edit.getText()).toString().trim();
            OperationResult<Void> validationResult = authViewModel.validateRegistrationEmail(email,
                    terms.isChecked());
            if (!validationResult.isSuccess()) {
                if ("You must accept the terms and conditions".equals(validationResult.getMessage())) {
                    terms_text.setTextColor(ColorStateList.valueOf(Color.RED));
                    return;
                }
                email_text.setErrorEnabled(true);
                email_text.setError(validationResult.getMessage());
                email_text.setDefaultHintTextColor(ColorStateList.valueOf(Color.RED));
            } else {
                Intent intent = new Intent(RegisterActivity.this, RegisterActivityPass.class);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            }
        });
    }
}
