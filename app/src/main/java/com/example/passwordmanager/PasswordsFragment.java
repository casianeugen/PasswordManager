package com.example.passwordmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passwordmanager.databinding.FragmentPasswordsBinding;

import java.util.List;

public class PasswordsFragment extends Fragment {
    private FragmentPasswordsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPasswordsBinding.inflate(inflater, container, false);
        LocalRepository repository = LocalRepository.getInstance(requireContext());
        List<VaultItemEntity> data = repository.getPasswordItemsForCurrentUser();

        RecyclerView recyclerView = binding.recyclerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(new CustomAdapter(data, requireActivity()));
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
