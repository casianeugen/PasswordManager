package com.example.passwordmanager.ui.generate_pass;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GeneratePassViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public GeneratePassViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is generate_pass fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}