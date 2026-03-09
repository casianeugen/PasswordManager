package com.example.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

public class BiometricActivity extends AppCompatActivity {
    private AuthViewModel authViewModel;
    private UserEntity currentUser;

        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        currentUser = authViewModel.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(BiometricActivity.this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_biometrics);

        MaterialButton bioLater = findViewById(R.id.bioLater);
        MaterialButton bioTurn = findViewById(R.id.bioTurn);

        bioLater.setOnClickListener(v -> {
            authViewModel.updateBiometricEnrollment(false);
            openMain();
        });

        bioTurn.setOnClickListener(v -> {
            BiometricManager biometricManager = BiometricManager.from(this);
            int canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK);
            if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
                showBiometricPrompt();
            } else {
                authViewModel.updateBiometricEnrollment(false);
                Toast.makeText(this, "Biometrics are not available on this device", Toast.LENGTH_SHORT).show();
                openMain();
            }
        });
    }

    private void showBiometricPrompt() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Authenticate with your fingerprint")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK)
                .setNegativeButtonText("Cancel")
                .build();

        BiometricPrompt.AuthenticationCallback authenticationCallback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                authViewModel.updateBiometricEnrollment(true);
                openMain();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(BiometricActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
            }
        };

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, ContextCompat.getMainExecutor(this), authenticationCallback);
        biometricPrompt.authenticate(promptInfo);
    }

    private void openMain() {
        Intent intent = new Intent(BiometricActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
