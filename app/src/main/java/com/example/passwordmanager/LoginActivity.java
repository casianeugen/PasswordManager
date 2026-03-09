package com.example.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText login_mail;
    private TextInputEditText login_pass;
    private LinearLayout bio;
    private TextInputLayout emailError;
    private TextInputLayout passError;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        if (authViewModel.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        MaterialTextView reg_link = findViewById(R.id.reg_text);
        MaterialButton button_log_in = findViewById(R.id.button_log_in);

        bio = findViewById(R.id.bio);
        emailError = findViewById(R.id.log_email_text);
        passError = findViewById(R.id.log_pass_text);
        login_mail = findViewById(R.id.log_email);
        login_pass = findViewById(R.id.log_pass);
        bio.setVisibility(View.GONE);

        String requestedEmail = getIntent().getStringExtra("email");
        if (requestedEmail != null) {
            login_mail.setText(requestedEmail);
        }

        UserEntity lastLoggedInUser = authViewModel.getLastLoggedInUser();
        if (lastLoggedInUser != null) {
            if (requestedEmail == null || requestedEmail.trim().isEmpty()) {
                login_mail.setText(lastLoggedInUser.email);
            }
            if (lastLoggedInUser.biometricEnrolled) {
                bio.setVisibility(View.VISIBLE);
            }
        }

        login_mail.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                emailError.setErrorEnabled(false);
            }
        });

        login_pass.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                passError.setErrorEnabled(false);
            }
        });

        login_mail.addTextChangedListener(new BioLogin(bio, login_mail, authViewModel));
        bio.setOnClickListener(v -> showBiometricPrompt());

        reg_link.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        TextView forgot_pass = findViewById(R.id.forgot_pass);
        forgot_pass.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));

        button_log_in.setOnClickListener(v -> {
            String email = Objects.requireNonNull(login_mail.getText()).toString().trim();
            String password = Objects.requireNonNull(login_pass.getText()).toString();
            OperationResult<UserEntity> result = authViewModel.login(email, password);
            if (result.isSuccess()) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                emailError.setErrorEnabled(true);
                emailError.setError(result.getMessage());
                passError.setErrorEnabled(true);
                passError.setError("Check your password and try again");
            }
        });
    }

    private void showBiometricPrompt() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Login")
                .setSubtitle("Log in with your biometric credentials")
                .setNegativeButtonText("Cancel")
                .build();

        BiometricPrompt biometricPrompt = new BiometricPrompt(LoginActivity.this,
                ContextCompat.getMainExecutor(this),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        loginWithBiometrics();
                    }
                });
        biometricPrompt.authenticate(promptInfo);
    }

    private void loginWithBiometrics() {
        String email = Objects.requireNonNull(login_mail.getText()).toString().trim();
        OperationResult<UserEntity> result = authViewModel.loginWithBiometrics(email);
        if (result.isSuccess()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            passError.setErrorEnabled(true);
            passError.setError(result.getMessage());
        }
    }
}
