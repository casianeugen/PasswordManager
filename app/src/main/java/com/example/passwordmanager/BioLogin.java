package com.example.passwordmanager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class BioLogin implements TextWatcher {
    private final LinearLayout bio;
    private final TextInputEditText log_email;
    private final String userID;

    public BioLogin(LinearLayout bio, TextInputEditText log_email, String userID) {
        this.bio = bio;
        this.log_email = log_email;
        this.userID = userID;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (userID != null) {
            db.collection("users").document(userID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists() &&
                                    Objects.equals(document.getBoolean("biometricEnrolled"), true)
                                    && Objects.equals(document.getString("email"), Objects.requireNonNull(log_email.getText()).toString().trim())) {
                                bio.setVisibility(View.VISIBLE);
                            } else {
                                // User doesn't exist in the database, hide the button
                                bio.setVisibility(View.GONE);
                            }
                        } else {
                            Log.d("User Document:", "Error when fetching the user's document");
                        }
                    });
        } else {
            // Handle the case when the user ID is not found in SharedPreferences
            bio.setVisibility(View.GONE);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
