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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import java.util.regex.Pattern;

public class RegisterActivityPass extends AppCompatActivity {
    @SuppressLint({"SetTextI18n", "RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_passwords);

        AuthViewModel authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
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
            Intent intent = getIntent();
            String email = intent.getStringExtra("email");
            String password = Objects.requireNonNull(newPass.getText()).toString();
            String confirmation = Objects.requireNonNull(reg_pass_confirm.getText()).toString();
            String hint = Objects.requireNonNull(pass_hint.getText()).toString().trim();
            OperationResult<UserEntity> result = authViewModel.register(email, password,
                    confirmation, hint);
            if (!result.isSuccess()) {
                String message = result.getMessage();
                if ("The password does not match".equals(message)) {
                    passConfirmLayout.setErrorEnabled(true);
                    passConfirmLayout.setError(message);
                    passConfirmLayout.setDefaultHintTextColor(ColorStateList.valueOf(Color.RED));
                } else {
                    newPassColor.setErrorEnabled(true);
                    newPassColor.setError(message);
                    newPassColor.setDefaultHintTextColor(ColorStateList.valueOf(Color.RED));
                }
                return;
            }

            if (result.getData() == null) {
                Toast.makeText(this, "Account already exists", Toast.LENGTH_SHORT).show();
            } else {
                BiometricManager biometricManager = BiometricManager.from(this);
                int canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK);
                Class<?> destination = canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS ? BiometricActivity.class : MainActivity.class;
                startActivity(new Intent(RegisterActivityPass.this, destination));
                finish();
            }
        });
    }
}
