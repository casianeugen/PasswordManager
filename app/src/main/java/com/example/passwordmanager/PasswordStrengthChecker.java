package com.example.passwordmanager;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;
import java.util.regex.Pattern;

public class PasswordStrengthChecker implements TextWatcher {
    private final ProgressBar strengthTextView;
    private final Context mContext;
    private final TextInputEditText newPass;
    private final TextView passLower;
    private final TextView passUpper;
    private final TextView passLong;
    private final TextView passNum;

    public PasswordStrengthChecker(ProgressBar strengthTextView, Context context,
                                   TextInputEditText newPass, TextView passLower,
                                   TextView passUpper, TextView passLong, TextView passNum) {
        this.strengthTextView = strengthTextView;
        this.mContext = context;
        this.newPass = newPass;
        this.passLower = passLower;
        this.passUpper = passUpper;
        this.passLong = passLong;
        this.passNum = passNum;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int passwordStrength = getPasswordStrength(s.toString());
        strengthTextView.setProgress(passwordStrength);
        if(passwordStrength <= 2 && passwordStrength >= 0)
            strengthTextView.setProgressTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(mContext, R.color.red)));
        if(passwordStrength > 2 && passwordStrength <= 4)
            strengthTextView.setProgressTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(mContext, R.color.orange)));
        if(passwordStrength > 4 && passwordStrength <= 6)
            strengthTextView.setProgressTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(mContext, R.color.shadow_green)));
        if(passwordStrength > 6 && passwordStrength <= 8)
            strengthTextView.setProgressTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(mContext, R.color.green)));
        if (Objects.requireNonNull(newPass.getText()).toString().trim().length() < 8) {
            passLong.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unchecked_condition,0,0,0);
        }
        else if (!Pattern
                .compile("(?=.*[A-Z])")
                .matcher(newPass.getText().toString().trim()).find()){
            passUpper.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unchecked_condition,0,0,0);
        }
        else if (!Pattern
                .compile("(?=.*[a-z])")
                .matcher(newPass.getText().toString().trim()).find()){
            passLower.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unchecked_condition,0,0,0);
        }
        else if (!Pattern
                .compile("(?=.*\\d).*")
                .matcher(newPass.getText().toString().trim()).find()){
            passNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unchecked_condition,0,0,0);
        }
        if (TextUtils.isEmpty(newPass.getText().toString().trim())){
            passLower.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unchecked_condition,0,0,0);
            passNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unchecked_condition,0,0,0);
            passUpper.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unchecked_condition,0,0,0);
            passLong.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unchecked_condition,0,0,0);
            strengthTextView.setProgress(0);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private int getPasswordStrength(String password) {
        int score = 0;
        if (password.length() <= 8) {
            score += 1;
        } else if (password.length() <= 12) {
            score += 2;
            passLong.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checked_condition,0,0,0);
        } else {
            score += 3;
        }

        if (password.matches("(?=.*[A-Z]).*")) {
            score += 2;
            passUpper.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checked_condition,0,0,0);
        }

        if (password.matches("(?=.*[a-z]).*")) {
            score += 1;
            passLower.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checked_condition,0,0,0);
        }

        if (password.matches("(?=.*\\d).*")) {
            score += 1;
            passNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checked_condition,0,0,0);
        }

        return score;
    }
}