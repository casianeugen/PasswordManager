package com.example.passwordmanager;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TermsActivity  extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        TextView line1 = findViewById(R.id.line1);
        TextView line2 = findViewById(R.id.line2);
        TextView line3 = findViewById(R.id.line3);
        TextView line4 = findViewById(R.id.line4);
        TextView line5 = findViewById(R.id.line5);
        TextView line6 = findViewById(R.id.line6);

        SpannableString s1 = new SpannableString(line1.getText().toString());
        SpannableString s2 = new SpannableString(line2.getText().toString());
        SpannableString s3 = new SpannableString(line3.getText().toString());
        SpannableString s4 = new SpannableString(line4.getText().toString());
        SpannableString s5 = new SpannableString(line5.getText().toString());
        SpannableString s6 = new SpannableString(line6.getText().toString());

        s1.setSpan(new StyleSpan(Typeface.BOLD), 0, 28, 0);
        s2.setSpan(new StyleSpan(Typeface.BOLD), 0, 12, 0);
        s3.setSpan(new StyleSpan(Typeface.BOLD), 0, 16, 0);
        s4.setSpan(new StyleSpan(Typeface.BOLD), 0, 16, 0);
        s5.setSpan(new StyleSpan(Typeface.BOLD), 0, 28, 0);
        s6.setSpan(new StyleSpan(Typeface.BOLD), 0, 17, 0);

        line1.setText(s1);
        line2.setText(s2);
        line3.setText(s3);
        line4.setText(s4);
        line5.setText(s5);
        line6.setText(s6);

    }
}
