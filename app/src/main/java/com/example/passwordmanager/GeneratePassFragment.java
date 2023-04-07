package com.example.passwordmanager;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.security.SecureRandom;
import java.util.Random;

public class GeneratePassFragment extends DialogFragment {
    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";

    private TextView generated_pass;
    private TextView count_pass_length;
    private SeekBar seek_bar_password;
    @NonNull
    @Override
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
            Intent intent = new Intent(getActivity(), RegisterActivity.class);
            startActivity(intent);
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
