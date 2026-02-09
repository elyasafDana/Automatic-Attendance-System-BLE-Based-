package com.example.adplusscan;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import kotlinx.serialization.json.Json;
//import kotlinx.serialization.json.JsonObject;

public class bleViewModle {
    private  String TAG="asd";
    private  BluetoothLeScanner scanner= BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();;

    private static final long SCAN_PERIOD = 10000;

    private bleRepository bleRepo=new bleRepository();

    private supabaseRepository supaRepo=supabaseRepository.singeltonBuilder();

    private MutableLiveData<Boolean> uploadedDone=new MutableLiveData<>();
    private String phoneId;
    private static final int REQUEST_PERMISSION = 1; // serial idenify of the premission request
    private String studentId;


    public MutableLiveData<Boolean> getuploadedDone() {
        return uploadedDone;
    }

    public String[] cheakPermissionRequired( Activity activity){ // return the not available premissions
        List<String> temp =new ArrayList<>();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {

            if(checkSelfPermission(activity,Manifest.permission.BLUETOOTH_CONNECT)==PackageManager.PERMISSION_DENIED ){
                temp.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
            if (checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_DENIED) {
                temp.add(Manifest.permission.BLUETOOTH_ADVERTISE);
            }

            if (checkSelfPermission(activity,Manifest.permission.BLUETOOTH_SCAN)==PackageManager.PERMISSION_DENIED){
                temp.add(Manifest.permission.BLUETOOTH_SCAN);
            }

        }
        if (checkSelfPermission(activity,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_DENIED){
            //Log.d(TAG,"fine location not granted");
            temp.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        String print=" this is the premission list: ";
        for (int i=0;i<temp.size();i++) {
            print=print+temp.get(i);
        }
        Log.d(TAG,print);
        return temp.toArray(new String[0]);

    }

    public boolean isBleAvailable(){
        return bleRepo.BleSettingsAvailable();
    }
    public boolean isAdvertising(){
       return bleRepo.isAdvertising();
    }

    public boolean isScannerOn(){
        return bleRepo.isIsScannerOn();
    }

    public void startAd(String uuid,Consumer<Boolean> callBack){
        if (bleRepo.isAdvertising()){
            Log.d("asd","already advertising");
            return;
        }

        supaRepo.getInfo("/lessons?select=lesson_id&date=eq."+getDate()+"&teacher_id=eq."+uuid+"",jsonResponse-> {

            String lessonID = jsonResponse.get(0).getAsJsonObject().get("lesson_id").getAsString();
            Log.d("asd", "the lesson id is" + lessonID);

            UUID password=UUID.randomUUID();
            JsonObject data=new JsonObject();
            data.addProperty("password",password.toString());



            updateValue("lessons?lesson_id=eq."+lessonID,data, isvalid-> {
                if (isvalid) {
                    Log.d("asd","password uploaded");
                    JsonObject phoneData=new JsonObject();
                    String phoneId=bleRepo.generateRandomString();
                    phoneData.addProperty("phone_id",phoneId);
                    updateValue("teacher?teacher_id=eq."+uuid,phoneData,isvalid1->{

                        if (isvalid1){
                            Log.d(TAG, "phone posted to supa");
                            bleRepo.startAd(phoneId,password);
                            callBack.accept(true);
                        }else {
                            callBack.accept(false);
                            Log.d(TAG, "data was not posted to the supabase");
                        }
                    });
                } else {
                    callBack.accept(false);
                    Log.d(TAG, "data was not posted to the supabase");
                }
            });

                });
    }
    public  void stopAd(){
        bleRepository.stopAd();
    }
    public String getDate(){
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return today.format(formatter);

    }

    public  void changeToPresence(String p_password,String uuid){
        JsonObject d=new JsonObject();
        d.addProperty("p_password",p_password);
        d.addProperty("p_date",getDate());
        d.addProperty("studentid",uuid);
        supaRepo.postValue("/rpc/mark_attendance_final",d,isvalid->{

        });
    }
    public void updateValue(String url, JsonObject data, Consumer<Boolean> callBack) {
        supaRepo.updateValue(url,data,callBack);
    }

    public void postValue(String url, JsonObject data, Consumer<Boolean> callBack){
        supaRepo.postValue(url,data,callBack);
    }

    public void getInfo(String url, Consumer<JsonArray> callBack){
        supaRepo.getInfo(url,callBack);
    }



    public void scanLeDevice(String studentId){
        this.studentId=studentId;
        //this.phoneId=phoneId;
        String date=getDate();
        getInfo("/student_on_lesson?select=student_id,lessons!inner(date,teacher(phone_id))&student_id=eq."+studentId+"&lessons.date=eq."+date,jsonResponse->{
            this.phoneId=jsonResponse.get(0).getAsJsonObject().get("lessons").getAsJsonObject().get("teacher").getAsJsonObject().get("phone_id").getAsString();
            Log.d("asd","the phone id is: "+phoneId);
            new Handler(Looper.getMainLooper()).post(() -> {

                // עכשיו כאן מותר ליצור Handler חדש (או להשתמש בקיים)
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "scanning time passed! stoping scan");
                        scanner.stopScan(leScanCallback);
                    }
                }, SCAN_PERIOD);

                Log.d(TAG, "start scanning");
                scanner.startScan(leScanCallback);
            });
        });
    }



    private final ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d(TAG,"got some device");

            byte[] manufacturerData = result.getScanRecord().getManufacturerSpecificData(0x004C);
            String manufacturerName= Arrays.toString(manufacturerData);// return the manufacterer id
            int rssi=result.getRssi();

                if (manufacturerData == null) return;
                if (Arrays.equals(phoneId.getBytes(), manufacturerData)) {
                    Log.d("asd", "found the theacher phone!!");
                    String passwordString = "";
                    if (result.getScanRecord().getServiceUuids() == null) {
                        passwordString = " null";
                    } else {
                        passwordString = result.getScanRecord().getServiceUuids().toString();
                        passwordString = passwordString.substring(1, passwordString.length() - 1);
                    }

                    Log.d(TAG,
                            " | RSSI: " + result.getRssi() +
                                    " | UUIDs: " + passwordString + "Manufacturer Data: " + Arrays.toString(manufacturerData));
                    if (rssi<-50){
                        Log.d("asd","you are too far");
                        uploadedDone.setValue(false);
                        return;
                    }
                    changeToPresence(passwordString, studentId);
                    //scanner.stopScan(leScanCallback);
                    uploadedDone.setValue(true);


            }

        }
        @Override
        public void onScanFailed(int errorCode) {
            // פונקציה שמופעלת אם הסריקה נכשלת
            Log.d(TAG,"BLE scanning faild"+ errorCode);
        }
    };

    public  boolean GpsIsOn(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        }
        return true;
    }





}
