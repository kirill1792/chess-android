package com.kirill1636.chessmate.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> mText1;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
        mText1 = new MutableLiveData<>();
        mText1.setValue("Fallos poten!Potny fallos vsem vam v anus!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}