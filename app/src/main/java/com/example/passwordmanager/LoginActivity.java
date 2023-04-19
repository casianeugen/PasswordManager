package com.example.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    TextInputEditText login_mail;
    TextInputEditText login_pass;
    LinearLayout bio;
    SharedPreferencesHelper sph;
    private TextInputLayout emailError;
    private TextInputLayout passError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sph = new SharedPreferencesHelper(this);
        MaterialTextView reg_link = findViewById(R.id.reg_text);
        MaterialButton button_log_in = findViewById(R.id.button_log_in);
        bio = findViewById(R.id.bio);
        emailError = findViewById(R.id.log_email_text);
        passError = findViewById(R.id.log_pass_text);

        login_mail = findViewById(R.id.log_email);
        login_pass = findViewById(R.id.log_pass);
        bio.setVisibility(View.GONE);
        String lastLoggedInUserId = sph.getStringValue("lastLoginUserID");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

//        if(lastLoggedInUserId != null){
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }

        login_mail.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                emailError.setErrorEnabled(false);
            }
        });

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && Objects.equals(lastLoggedInUserId, user.getUid())) {
            login_mail.setText(user.getEmail());
            bio.setVisibility(View.VISIBLE);
        }

        login_pass.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                passError.setErrorEnabled(false);
            }
        });

        login_mail.addTextChangedListener(new BioLogin(bio, login_mail));
        bio.setOnClickListener(v -> showBiometricPrompt());

        reg_link.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        TextView forgot_pass = findViewById(R.id.forgot_pass);

        forgot_pass.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        button_log_in.setOnClickListener(v -> {
            try {
                login_mail = findViewById(R.id.log_email);
                login_pass = findViewById(R.id.log_pass);
                String email = Objects.requireNonNull(login_mail.getText()).toString().trim();
                String password = Objects.requireNonNull(login_pass.getText()).toString();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        sph.saveStringValue("lastLoginUserID", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        emailError.setErrorEnabled(true);
                        emailError.setError("Check your email address and try again");

                        passError.setErrorEnabled(true);
                        passError.setError("Check your password and try again");
                    }
                });
            } catch (IllegalArgumentException ex) {
                emailError.setErrorEnabled(true);
                emailError.setError("Check your email address and try again");

                passError.setErrorEnabled(true);
                passError.setError("Check your password address and try again");
            }
        });
    }

    private void showBiometricPrompt() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Biometric Login").setSubtitle("Log in with your biometric credentials").setNegativeButtonText("Cancel").build();

        BiometricPrompt biometricPrompt = new BiometricPrompt(LoginActivity.this, ContextCompat.getMainExecutor(this), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                // Handle authentication error
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                // Perform login action
                loginWithBiometrics();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                // Handle authentication failure
            }
        });
        biometricPrompt.authenticate(promptInfo);
    }

    private void loginWithBiometrics() {
        String userId = sph.getStringValue("lastLoginUserID");
        if (userId != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            // Handle the case when the user's document is not found
            Log.d("User Document:", "User's document is not found");
        } else {
            // Handle the case when the user ID is not found in SharedPreferences
            Log.d("SharedPreferences:", "ID is not found in SharedPreferences");
        }
    }
}
