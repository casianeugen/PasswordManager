package com.example.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class CustomBottomSheetDialogFragment extends BottomSheetDialogFragment implements MenuItem.OnMenuItemClickListener {
    private final DocumentSnapshot document;
    private final int position;
    private final CustomAdapter adapter;

    public CustomBottomSheetDialogFragment(DocumentSnapshot document, int position, CustomAdapter adapter) {
        this.document = document;
        this.position = position;
        this.adapter = adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_menu, container, false);
        // Find and set click listeners for your menu items
        TextView title = view.findViewById(R.id.menu_title);
        title.setText(document.getString("3)Name"));
        view.findViewById(R.id.edit).setOnClickListener(v -> handleEdit());
        view.findViewById(R.id.delete).setOnClickListener(v -> handleDelete());
        view.findViewById(R.id.view).setOnClickListener(v -> handleView());
        return view;
    }

    private void handleEdit() {
        Intent intent;
        String type = Objects.requireNonNull(document.getString("1)Type"));
        if (Objects.equals(type, "payment_card"))
            intent = new Intent(requireActivity(), AddPaymentCardActivity.class);
        else intent = new Intent(requireActivity(), AddPasswordActivity.class);
        intent.putExtra("documentId", document.getId());
        startActivity(intent);
        requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private void handleDelete() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null && document.getString("1)Type") != null) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection(Objects.requireNonNull(document.getString("1)Type")))
                    .document(document.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        if (adapter != null) {
                            adapter.deleteItem(position);
                        }
                        dismiss();
                    });
        }
    }

    private void handleView() {
        String type = Objects.requireNonNull(document.getString("1)Type"));
        String name = document.getString("3)Name");
        String content;
        if (Objects.equals(type, "payment_card")) content = document.getString("6)Number");
        else content = document.getString("6)Password");

        DialogView dialog = new DialogView(requireActivity(), name, content);
        dialog.show();
        dismiss();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return true;
    }
}