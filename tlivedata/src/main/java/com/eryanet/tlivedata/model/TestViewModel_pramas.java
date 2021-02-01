package com.eryanet.tlivedata.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TestViewModel_pramas extends ViewModel {

    private final String mKey;
    private MutableLiveData<String> mNameEvent = new MutableLiveData<>();

    public MutableLiveData<String> getNameEvent() {
        return mNameEvent;
    }

    public TestViewModel_pramas(String key) {
        mKey = key;
    }

    public static class Factory implements ViewModelProvider.Factory {
        private String mKey;

        public Factory(String key) {
            mKey = key;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new TestViewModel_pramas(mKey);
        }
    }

    public String getKey() {
        return mKey;
    }
}