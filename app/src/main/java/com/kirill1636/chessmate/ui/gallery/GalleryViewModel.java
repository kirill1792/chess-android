package com.kirill1636.chessmate.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> mText1;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
        mText1 = new MutableLiveData<>();
        mText1.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}