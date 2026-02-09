package com.example.adplusscan;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class bleRepository {

    private static final int REQUEST_PERMISSION = 1; // serial idenify of the premission request


    private MutableLiveData<String[]> premissionRequired=new MutableLiveData<>();
    private static final String TAG = "asd";
    private static boolean isAdvertising=false;
    private static boolean isScannerOn=false;

    public boolean isAdvertising(){
        return isAdvertising;
    }

    public boolean isIsScannerOn(){
        return isScannerOn;
    }


    public static void startAd(String phoneId,UUID password){
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        BluetoothLeAdvertiser advertiser= bluetoothAdapter.getBluetoothLeAdvertiser();
        Log.d(TAG,"create ADVETISER");
        if (advertiser==null){Log.d(TAG,"AD IS NULL!!!"); return;}

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(false)
                .build();

        AdvertiseData data=new AdvertiseData.Builder()
                .addServiceUuid(new ParcelUuid(password))
                //.addServiceData(new ParcelUuid(password),phoneId.getBytes())
                //.setIncludeDeviceName(true)
                .addManufacturerData(0x004C, phoneId.getBytes())
                .build();

        Log.d(TAG,"set all the AD objects");

        advertiser.startAdvertising( settings, data,advertiseCallback );

    }


    private static AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            isAdvertising = true;
            Log.d(TAG, "Successfully started advertising");
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            isAdvertising = false;
            Log.e(TAG, "Advertising failed with error code: " + errorCode);
        }
    };
    
    public static void stopAd(){
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        BluetoothLeAdvertiser advertiser= bluetoothAdapter.getBluetoothLeAdvertiser();
        if (isAdvertising){
            advertiser.stopAdvertising(advertiseCallback);
        }
    }


    public String generateRandomString() {
        // כל התווים שמהם נרצה להרכיב את המילה
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        // לולאה שרצה 7 פעמים כדי לבחור 7 אותיות
        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(alphabet.length());
            char randomChar = alphabet.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }





    public static boolean BleSettingsAvailable(){
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter==null){
            Log.d(TAG,"you have no bluetooth device on your phone!!");
            return false;
        }

        if (!bluetoothAdapter.isEnabled()){
            Log.d(TAG,"please enable bluetooth!!!!!");
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT_CODE);
            return false;
        }

        if(!bluetoothAdapter.isMultipleAdvertisementSupported()){
            Log.d(TAG,"your phone does not support BLE!!");
            return false;
        }
        Log.d(TAG,"all bluetooth settings are good");
        return true;
    }

}
