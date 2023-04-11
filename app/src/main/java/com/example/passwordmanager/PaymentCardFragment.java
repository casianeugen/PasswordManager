package com.example.passwordmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.passwordmanager.R;
import com.example.passwordmanager.databinding.FragmentPaymentCardBinding;

public class PaymentCardFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_payment_card, container, false);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}