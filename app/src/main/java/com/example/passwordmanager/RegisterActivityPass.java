package com.example.passwordmanager;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class RegisterActivityPass extends AppCompatActivity {
    @SuppressLint({"SetTextI18n", "RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_passwords);

        TextInputLayout passConfirmLayout = findViewById(R.id.reg_pass_confirm_text);
        TextInputEditText reg_pass_confirm = findViewById(R.id.reg_pass_confirm);
        ProgressBar passStrength = findViewById(R.id.passwordStrengthBar);
        TextInputLayout newPassColor = findViewById(R.id.reg_pass_text);
        Button continueButton = findViewById(R.id.button_continue_reg);
        TextInputEditText pass_hint = findViewById(R.id.pass_hint);
        TextInputEditText newPass = findViewById(R.id.reg_pass);
        LinearLayout conditions = findViewById(R.id.conditions);
        TextView passLower = findViewById(R.id.passLower);
        TextView passUpper = findViewById(R.id.passUpper);
        TextView passLong = findViewById(R.id.passLong);
        TextView passNum = findViewById(R.id.passNum);

        conditions.setVisibility(View.GONE);

        ObjectAnimator animator = ObjectAnimator.ofFloat(conditions, "alpha", 0f, 1f);
        animator.setDuration(800);

        newPass.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                conditions.setVisibility(View.VISIBLE);
                animator.start();
                passLong.setText("At least 8 characters long");
                passNum.setText("At least 1 number");
                passLower.setText("At least 1 lowercase letter");
                passUpper.setText("At least 1 uppercase letter");
            } else {
                conditions.setVisibility(View.GONE);
            }
        });

        reg_pass_confirm.setOnClickListener(v -> conditions.setVisibility(View.GONE));
        newPass.addTextChangedListener(new PasswordStrengthChecker(passStrength, this, newPass, passLower, passUpper, passLong, passNum));
        continueButton.setOnClickListener(v -> {
            BiometricManager biometricManager = BiometricManager.from(this);
            int canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK);
            if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
                if (Objects.requireNonNull(newPass.getText()).toString().trim().length() < 8) {
                    newPassColor.setErrorEnabled(true);
                    newPassColor.setError("Your password is too short");
                    newPassColor.setDefaultHintTextColor(ColorStateList.valueOf(Color.RED));
                } else if (!Pattern
                        .compile("(?=.*[A-Z])")
                        .matcher(newPass.getText().toString().trim()).find()) {
                    newPassColor.setErrorEnabled(true);
                    newPassColor.setError("Your password must contain at least one uppercase letter");
                    newPassColor.setDefaultHintTextColor(ColorStateList.valueOf(Color.RED));
                } else if (!Pattern
                        .compile("(?=.*[a-z])")
                        .matcher(newPass.getText().toString().trim()).find()) {
                    newPassColor.setErrorEnabled(true);
                    newPassColor.setError("Your password must contain at least one lowercase letter");
                    newPassColor.setDefaultHintTextColor(ColorStateList.valueOf(Color.RED));
                } else if (!Pattern
                        .compile("(?=.*\\d).*")
                        .matcher(newPass.getText().toString().trim()).find()) {
                    newPassColor.setErrorEnabled(true);
                    newPassColor.setError("Your password must contain at least one number");
                    newPassColor.setDefaultHintTextColor(ColorStateList.valueOf(Color.RED));
                } else if (!newPass.getText().toString().equals(Objects.requireNonNull(reg_pass_confirm.getText()).toString())) {
                    passConfirmLayout.setErrorEnabled(true);
                    passConfirmLayout.setError("The password does not match");
                    passConfirmLayout.setDefaultHintTextColor(ColorStateList.valueOf(Color.RED));
                } else {
                    Intent i = getIntent();
                    String email = i.getStringExtra("email");
                    String password = newPass.getText().toString();
                    String hint = Objects.requireNonNull(pass_hint.getText()).toString().trim();

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseFirestore db1 = FirebaseFirestore.getInstance();

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("email", email);
                                    userMap.put("password_hint", hint);

                                    assert user != null;
                                    db1.collection("users").document(user.getUid())
                                            .set(userMap)
                                            .addOnSuccessListener(aVoid -> {
                                                Intent intent = new Intent(RegisterActivityPass.this, BiometricActivity.class);
                                                intent.putExtra("user", user);
                                                startActivity(intent);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(this, "Error adding user", Toast.LENGTH_SHORT).show());

                                }
                            });
                }
            } else {
                Intent intent = new Intent(RegisterActivityPass.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

        });
    }
}
