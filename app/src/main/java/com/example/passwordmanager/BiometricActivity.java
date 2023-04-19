package com.example.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BiometricActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometrics);

        MaterialButton bioLater = findViewById(R.id.bioLater);
        MaterialButton bioTurn = findViewById(R.id.bioTurn);

        bioLater.setOnClickListener(v -> {
            db = FirebaseFirestore.getInstance();
            updateBiometricEnrollmentFlag(Objects.requireNonNull(mAuth.getCurrentUser()).getUid(), false);
            Intent intent = new Intent(BiometricActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        bioTurn.setOnClickListener(v -> {
            db = FirebaseFirestore.getInstance();
            showBiometricPrompt();
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
                updateBiometricEnrollmentFlag(Objects.requireNonNull(mAuth.getCurrentUser()).getUid(), true);
                Intent intent = new Intent(BiometricActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
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

    private void updateBiometricEnrollmentFlag(String userId, boolean isEnrolled) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("biometricEnrolled", isEnrolled);

        db.collection("users").document(userId)
                .set(userData, SetOptions.merge())
                .addOnSuccessListener(unused -> {

                })
                .addOnFailureListener(e -> Toast.makeText(BiometricActivity.this, "Register Error", Toast.LENGTH_SHORT).show());
    }
}
