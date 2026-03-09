package com.example.passwordmanager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class BioLogin implements TextWatcher {
    private final LinearLayout bio;
    private final TextInputEditText mail;
    private final AuthViewModel authViewModel;

    public BioLogin(LinearLayout bio, TextInputEditText mail, AuthViewModel authViewModel) {
        this.bio = bio;
        this.mail = mail;
        this.authViewModel = authViewModel;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean showBiometric = authViewModel.canUseBiometric(
                Objects.requireNonNull(mail.getText()).toString());
        bio.setVisibility(showBiometric ? View.VISIBLE : View.GONE);
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
