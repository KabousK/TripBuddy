package com.example.formative_eduv4834254.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SlideshowViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SlideshowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is a placeholder for Memories/Budget/Registration");
    }

    public LiveData<String> getText() {
        return mText;
    }
}