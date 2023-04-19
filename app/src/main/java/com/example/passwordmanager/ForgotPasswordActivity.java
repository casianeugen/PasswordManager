package com.example.passwordmanager;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {
    private TextInputLayout email_text;
    private FirebaseAuth auth;
    TextInputEditText email;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setHomeAsUpIndicator(R.drawable.arrow_back);
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        email = findViewById(R.id.forgot_mail_value);
        auth = FirebaseAuth.getInstance();
        email_text = findViewById(R.id.forgot_mail_text);
        MaterialButton reg_continue = findViewById(R.id.button_mail);
        MaterialButton hint_email = findViewById(R.id.hint_email);
        reg_continue.setOnClickListener(v -> restorePass());
        hint_email.setOnClickListener(v -> restorePass());
    }
    public void restorePass(){
        try {
            auth.fetchSignInMethodsForEmail(Objects.requireNonNull(email.getText()).toString())
                    .addOnCompleteListener(task -> {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            email_text.setErrorEnabled(true);
                            email_text.setError("Enter a valid email address");
                            email_text.setDefaultHintTextColor(ColorStateList.valueOf(Color.RED));
                        } else {
                            boolean check = !Objects.requireNonNull(task.getResult().getSignInMethods()).isEmpty();
                            if (check) {
                                auth.sendPasswordResetEmail(email.getText().toString());
                                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                intent.putExtra("email", email.getText().toString());
                                startActivity(intent);
                                Toast.makeText(this, "Email for password reset was sent.", Toast.LENGTH_LONG).show();
                            } else {
                                email_text.setErrorEnabled(true);
                                email_text.setError("Email does not exist");
                                email_text.setDefaultHintTextColor(ColorStateList.valueOf(Color.RED));
                            }
                        }
                    });
        } catch (IllegalArgumentException ex) {
            email_text.setErrorEnabled(true);
            email_text.setError("Field is empty");
            email_text.setDefaultHintTextColor(ColorStateList.valueOf(Color.RED));
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
