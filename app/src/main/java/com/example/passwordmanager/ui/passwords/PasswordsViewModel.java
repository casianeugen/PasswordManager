package com.example.passwordmanager.ui.passwords;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PasswordsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PasswordsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is passwords fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}