package com.example.passwordmanager;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

public class DialogView extends Dialog {

    public DialogView(Context context, String title, String password) {
        super(context);
        setContentView(R.layout.view_dialog);
        setCancelable(true);
        TextView view_title = findViewById(R.id.view_title);
        TextView view_password = findViewById(R.id.view_password);
        Button view_ok = findViewById(R.id.view_ok);

        view_title.setText("Viewing " + title);
        view_password.setText(password);
        view_ok.setOnClickListener(v -> dismiss());


    }

}
