package com.example.passwordmanager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class BioLogin implements TextWatcher {
    private final LinearLayout bio;
    private final TextInputEditText mail;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public BioLogin(LinearLayout bio, TextInputEditText mail) {
        this.bio = bio;
        this.mail = mail;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        CollectionReference collectionRef = db.collection("users");
        if (user != null) {
            DocumentReference docRef = collectionRef.document(user.getUid());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        if (Objects.equals(document.getBoolean("biometricEnrolled"), true)
                                && Objects.requireNonNull(mail.getText()).toString().equals(user.getEmail())) {
                            bio.setVisibility(View.VISIBLE);
                        } else {
                            // User doesn't exist in the database, hide the button
                            bio.setVisibility(View.GONE);
                        }
                    }
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
