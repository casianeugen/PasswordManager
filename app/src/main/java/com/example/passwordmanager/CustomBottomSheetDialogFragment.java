package com.example.passwordmanager;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CustomBottomSheetDialogFragment extends BottomSheetDialogFragment implements MenuItem.OnMenuItemClickListener {
    private final VaultItemEntity item;
    private final int position;
    private final CustomAdapter adapter;

    public CustomBottomSheetDialogFragment(VaultItemEntity item, int position, CustomAdapter adapter) {
        this.item = item;
        this.position = position;
        this.adapter = adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, android.os.Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_menu, container, false);
        TextView title = view.findViewById(R.id.menu_title);
        title.setText(item.name);
        view.findViewById(R.id.edit).setOnClickListener(v -> handleEdit());
        view.findViewById(R.id.delete).setOnClickListener(v -> handleDelete());
        view.findViewById(R.id.view).setOnClickListener(v -> handleView());
        return view;
    }

    private void handleEdit() {
        Intent intent;
        if (item.isPaymentCard()) {
            intent = new Intent(requireActivity(), AddPaymentCardActivity.class);
        } else {
            intent = new Intent(requireActivity(), AddPasswordActivity.class);
        }
        intent.putExtra("documentId", String.valueOf(item.id));
        startActivity(intent);
        dismiss();
    }

    private void handleDelete() {
        LocalRepository.getInstance(requireContext()).deleteVaultItem(item.id);
        if (adapter != null) {
            adapter.deleteItem(position);
        }
        dismiss();
    }

    private void handleView() {
        DialogView dialog = new DialogView(requireActivity(), item.name, item.getSensitiveContent());
        dialog.show();
        dismiss();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return true;
    }
}
