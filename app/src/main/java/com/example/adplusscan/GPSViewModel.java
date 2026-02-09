package com.example.adplusscan;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GPSViewModel {

    private final MutableLiveData<Boolean> gpsEnabled = new MutableLiveData<>();
    private final MutableLiveData<Boolean> showEnableGPSDialog = new MutableLiveData<>();

    public LiveData<Boolean> getGpsEnabled() {
        return gpsEnabled;
    }

    public LiveData<Boolean> getShowEnableGPSDialog() {
        return showEnableGPSDialog;
    }

    private final GPSHelper gpsHelper = new GPSHelper();

    public void checkGPS(Context context) {
        if (gpsHelper.isGPSEnabled(context)) {
            gpsEnabled.setValue(true);
        } else {
            gpsEnabled.setValue(false);
            showEnableGPSDialog.setValue(true);
        }
    }

    public void requestEnableGPS(Activity activity, int requestCode) {
        gpsHelper.requestEnableGPS(activity, requestCode);
    }
}
