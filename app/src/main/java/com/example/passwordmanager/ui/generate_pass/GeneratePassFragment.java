package com.example.passwordmanager.ui.generate_pass;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.passwordmanager.databinding.FragmentGeneratePassBinding;

public class GeneratePassFragment extends Fragment {

    private FragmentGeneratePassBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GeneratePassViewModel generatePassViewModel =
                new ViewModelProvider(this).get(GeneratePassViewModel.class);

        binding = FragmentGeneratePassBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGeneratePass;
        generatePassViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}