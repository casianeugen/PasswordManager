package com.example.passwordmanager;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class GeneratePassFragment extends DialogFragment {
    private TextView generated_pass;
    private TextView count_pass_length;
    private SeekBar seek_bar_password;

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        GeneratePassword generatePassword = new GeneratePassword();
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

        RadioGroup types = view.findViewById(R.id.types);
        RadioButton radioButton1 = view.findViewById(R.id.first);
        RadioButton radioButton2 = view.findViewById(R.id.second);
        RadioButton radioButton3 = view.findViewById(R.id.third);

        SwitchMaterial lower = view.findViewById(R.id.lowercase);
        SwitchMaterial upper = view.findViewById(R.id.uppercase);
        SwitchMaterial number = view.findViewById(R.id.numbers);
        SwitchMaterial symbol = view.findViewById(R.id.symbols);

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
        generated_pass.setText(generatePassword
                .seekBarGenerate(seek_bar_password.getProgress(),
                        lower.isChecked(),
                        upper.isChecked(),
                        number.isChecked(),
                        symbol.isChecked()));

        seek_bar_password.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                count_pass_length.setText(String.valueOf(seek_bar_password.getProgress()));
                if (radioButton1.isChecked()) {
                    generated_pass.setText(generatePassword
                            .seekBarGenerate(seek_bar_password.getProgress(),
                                    lower.isChecked(),
                                    upper.isChecked(),
                                    number.isChecked(),
                                    symbol.isChecked()));
                }
                if (radioButton2.isChecked()) {
                    generated_pass.setText(generatePassword
                            .easyToSayGenerate(seek_bar_password.getProgress(),
                                    lower.isChecked(),
                                    upper.isChecked(),
                                    number.isChecked(),
                                    symbol.isChecked()));
                }
                if (radioButton3.isChecked()) {
                    generated_pass.setText(generatePassword
                            .easyToReadGenerate(seek_bar_password.getProgress(),
                                    lower.isChecked(),
                                    upper.isChecked(),
                                    number.isChecked(),
                                    symbol.isChecked()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        types.setOnCheckedChangeListener((compoundButton, b) -> {
            if (radioButton1.isChecked()) {
                symbol.setEnabled(true);
                number.setEnabled(true);
                lower.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    symbol.setEnabled(upper.isChecked() || number.isChecked() || lower.isChecked());
                    upper.setEnabled(symbol.isChecked() || number.isChecked() || lower.isChecked());
                    number.setEnabled(upper.isChecked() || symbol.isChecked() || lower.isChecked());
                });
                upper.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    symbol.setEnabled(lower.isChecked() || number.isChecked() || upper.isChecked());
                    number.setEnabled(lower.isChecked() || symbol.isChecked() || upper.isChecked());
                    lower.setEnabled(symbol.isChecked() || number.isChecked() || upper.isChecked());
                });
                number.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    symbol.setEnabled(upper.isChecked() || lower.isChecked() || number.isChecked());
                    lower.setEnabled(upper.isChecked() || symbol.isChecked() || number.isChecked());
                    upper.setEnabled(symbol.isChecked() || lower.isChecked() || number.isChecked());
                });
                symbol.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    lower.setEnabled(upper.isChecked() || number.isChecked() || symbol.isChecked());
                    number.setEnabled(upper.isChecked() || lower.isChecked() || symbol.isChecked());
                    upper.setEnabled(lower.isChecked() || number.isChecked() || symbol.isChecked());
                });
                generated_pass.setText(generatePassword
                        .seekBarGenerate(seek_bar_password.getProgress(),
                                lower.isChecked(),
                                upper.isChecked(),
                                number.isChecked(),
                                symbol.isChecked()));
            }
            if (radioButton2.isChecked()) {
                symbol.setEnabled(false);
                symbol.setChecked(false);
                number.setEnabled(true);
                lower.setChecked(true);
                if(!upper.isChecked() && !number.isChecked())
                    lower.setEnabled(false);
                lower.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    upper.setEnabled(symbol.isChecked() || number.isChecked() || lower.isChecked());
                    number.setEnabled(upper.isChecked() || symbol.isChecked() || lower.isChecked());

                });
                upper.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    lower.setEnabled(symbol.isChecked() || number.isChecked() || upper.isChecked());
                    number.setEnabled(symbol.isChecked() || lower.isChecked() || upper.isChecked());

                });
                number.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    lower.setEnabled(upper.isChecked() || symbol.isChecked() || number.isChecked());
                    upper.setEnabled(symbol.isChecked() || lower.isChecked() || number.isChecked());
                });
                generated_pass.setText(generatePassword
                        .easyToSayGenerate(seek_bar_password.getProgress(),
                                lower.isChecked(),
                                upper.isChecked(),
                                number.isChecked(),
                                symbol.isChecked()));
            }
            if (radioButton3.isChecked()) {
                number.setEnabled(false);
                number.setChecked(false);
                symbol.setEnabled(false);
                symbol.setChecked(false);
                lower.setChecked(true);
                if(!upper.isChecked())
                    lower.setEnabled(false);
                lower.setOnCheckedChangeListener((buttonView, isChecked) ->
                        upper.setEnabled(symbol.isChecked() || number.isChecked() || lower.isChecked()));
                upper.setOnCheckedChangeListener((buttonView, isChecked) ->
                        lower.setEnabled(symbol.isChecked() || number.isChecked() || upper.isChecked()));
                generated_pass.setText(generatePassword
                        .easyToReadGenerate(seek_bar_password.getProgress(),
                                lower.isChecked(),
                                upper.isChecked(),
                                number.isChecked(),
                                symbol.isChecked()));
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

        refresh.setOnClickListener(v -> {
            if (radioButton1.isChecked()) {
                generated_pass
                        .setText(generatePassword
                                .seekBarGenerate(seek_bar_password.getProgress(),
                                        lower.isChecked(),
                                        upper.isChecked(),
                                        number.isChecked(),
                                        symbol.isChecked()));
            }
            if (radioButton2.isChecked()) {
                generated_pass.setText(generatePassword
                        .easyToSayGenerate(seek_bar_password.getProgress(),
                                lower.isChecked(),
                                upper.isChecked(),
                                number.isChecked(),
                                symbol.isChecked()));
            }
            if (radioButton3.isChecked()) {
                generated_pass.setText(generatePassword
                        .easyToReadGenerate(seek_bar_password.getProgress(),
                                lower.isChecked(),
                                upper.isChecked(),
                                number.isChecked(),
                                symbol.isChecked()));
            }
        });

        cancel.setOnClickListener(v -> dismiss());

        use.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AddPasswordActivity.class);
            intent.putExtra("password", generated_pass.getText().toString());
            startActivity(intent);
        });

        return builder.create();
    }
}
