package com.example.passwordmanager;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.security.SecureRandom;

public class GeneratePassFragment extends DialogFragment {
    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";

    private TextView generated_pass;
    private TextView count_pass_length;
    private SeekBar seek_bar_password;
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_generate_pass, null);
        builder.setView(view);

        Button cancel = view.findViewById(R.id.cancel_button);
        Button use = view.findViewById(R.id.use_button);
        Button copy = view.findViewById(R.id.copy_pass);
        Button refresh = view.findViewById(R.id.refresh_pass);
        generated_pass = view.findViewById(R.id.generated_pass);
        count_pass_length = view.findViewById(R.id.count_length);
        seek_bar_password = view.findViewById(R.id.seek_bar_password);

        RadioButton radioButton1 = view.findViewById(R.id.first);
        RadioButton radioButton2 = view.findViewById(R.id.second);
        RadioButton radioButton3 = view.findViewById(R.id.third);

        SpannableString s1 = new SpannableString(radioButton1.getText().toString());
        SpannableString s2 = new SpannableString(radioButton2.getText().toString());
        SpannableString s3 = new SpannableString(radioButton3.getText().toString());

        s1.setSpan(new ForegroundColorSpan(Color.GRAY), 20, s1.length(), 0);
        s1.setSpan(new RelativeSizeSpan(0.8f), 20, s1.length(), 0);
        s2.setSpan(new ForegroundColorSpan(Color.GRAY), 12, s2.length(), 0);
        s2.setSpan(new RelativeSizeSpan(0.8f), 12, s2.length(), 0);
        s3.setSpan(new ForegroundColorSpan(Color.GRAY), 11, s3.length(), 0);
        s3.setSpan(new RelativeSizeSpan(0.8f), 11, s3.length(), 0);

        radioButton1.setText(s1);
        radioButton2.setText(s2);
        radioButton3.setText(s3);

        count_pass_length.setText(String.valueOf(seek_bar_password.getProgress()));
        generated_pass.setText(generatePassword(seek_bar_password.getProgress()));

        seek_bar_password.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                count_pass_length.setText(String.valueOf(seek_bar_password.getProgress()));
                generated_pass.setText(generatePassword(seek_bar_password.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        copy.setOnClickListener(v -> {
            ClipboardManager clipboardManager =
                    (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData =
                    ClipData.newPlainText("label", generated_pass.getText().toString());
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(getActivity(), "Password Copied", Toast.LENGTH_SHORT).show();
        });

        refresh.setOnClickListener(
                v -> generated_pass.setText(generatePassword(seek_bar_password.getProgress())));

        cancel.setOnClickListener(v -> dismiss());

        use.setOnClickListener(v -> {

        });

        return builder.create();
    }

    private String generatePassword(int length) {
        StringBuilder password = new StringBuilder(length);
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            password.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }
        return password.toString();
    }
}
