package com.example.passwordmanager.ui.payment_card;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PaymentCardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PaymentCardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is payment_card fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}