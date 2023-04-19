package com.example.passwordmanager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PaymentCardFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_payment_card, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        if (user != null) {
            db.collection("users").document(user.getUid()).collection("payment_card")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();
                        CustomAdapter adapter = new CustomAdapter(data, requireActivity());
                        recyclerView.setAdapter(adapter);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MainActivity", "Error retrieving data from Firestore", e);
                    });
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}