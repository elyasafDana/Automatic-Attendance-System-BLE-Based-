package com.example.adplusscan;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    BluetoothLeScanner scanner;
    private TextView textViewPrinter;
    private static final String TAG = "MY_APP_DEBUG";
    private static final long SCAN_PERIOD = 10000;

    BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        textViewPrinter = findViewById(R.id.textView);

        Button ScannerButton = findViewById(R.id.scan);

        Button AdButton= findViewById(R.id.ad);

        ScannerButton.setOnClickListener(v -> {
            if (!BleSettingsAvailable())return;
//            if(bluetoothAdapter==null){
//                Log.d(TAG,"you have no bluetooth device on your phone!!");
//            }
//            if(!bluetoothAdapter.isMultipleAdvertisementSupported()){
//                Log.d(TAG,"your phone does not support BLE!!");
//            }
//
//            if (!bluetoothAdapter.isEnabled()){
//                //bluetoothAdapter.enable();
//                Log.d(TAG,"please enable bluetooth!!!!!");
//            }
            scanner= BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
            sendRequestToUser();
            //scanLeDevice();
        });

        AdButton.setOnClickListener(V->{
            if (!BleSettingsAvailable())return;
           // if(!BleSettingsAvailable()){Log.d(TAG,"you have no bluetooth device on your phone!!");}
            //BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
//            if(bluetoothAdapter==null){
//                Log.d(TAG,"you have no bluetooth device on your phone!!");
//            }
//            if(!bluetoothAdapter.isMultipleAdvertisementSupported()){
//                Log.d(TAG,"your phone does not support BLE!!");
//            }
//
//            if (!bluetoothAdapter.isEnabled()){
//                //bluetoothAdapter.enable();
//                Log.d(TAG,"please enable bluetooth!!!!!");
//            }

            UUID uuid=UUID.randomUUID();
            //UUID uuid= UUID.fromString("0000FEAA-0000-1000-8000-00805F9B34FB");
            Log.d(TAG,"chose UUID");
            BluetoothLeAdvertiser advertiser= bluetoothAdapter.getBluetoothLeAdvertiser();
            Log.d(TAG,"create ADVETISER");
            if (advertiser==null){Log.d(TAG,"AD IS NULL!!!"); return;}

            AdvertiseSettings settings = new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                    .setConnectable(false)
                    .build();

            AdvertiseData data=new AdvertiseData.Builder()
                    .addServiceUuid(new ParcelUuid(uuid))
                    .setIncludeDeviceName(true)
                    .addManufacturerData(0x004C, new byte[] {0x01, 0x02, 0x03, 0x04})
                    .build();

            Log.d(TAG,"set all the AD objects");

            advertiser.startAdvertising( settings, data, new AdvertiseCallback() {
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    //super.onStartSuccess(settingsInEffect);
                    Log.d(TAG,"ad started");
                }

                @Override
                public void onStartFailure(int errorCode) {
                    Log.d(TAG,"BLE  Advertising failed: " + errorCode);
                }
            });



        });


    }

    //scanner= BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();



    private final ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

//            if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
//                    checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
            String uuidString ="";
            if (result.getScanRecord().getServiceUuids()==null)uuidString=" null";
            else{uuidString=result.getScanRecord().getServiceUuids().toString();}

            byte[] manufacturerData = result.getScanRecord().getManufacturerSpecificData(0x004C); // return the manufacterer id
            textViewPrinter.setText("UUIDs: " + uuidString);
            Log.d(TAG,"new BLE: " + result.getDevice().getName() +
                    " | RSSI: " + result.getRssi() +
                    " | UUIDs: " + uuidString + "Manufacturer Data: " + Arrays.toString(manufacturerData));
        }

        //        @Override
//        public void onBatchScanResults(List<ScanResult> results) {
//            // פונקציה שמופעלת אם המכשירים מגיעים בקבוצה (batch)
//            for (ScanResult result : results) {
//                System.out.println("new BLE" + result.getDevice().getName());
//            }
//        }
        @Override
        public void onScanFailed(int errorCode) {
            // פונקציה שמופעלת אם הסריקה נכשלת
            Log.d(TAG,"BLE scanning faild"+ errorCode);
        }
    };

    private void scanLeDevice(){
        Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
//                        checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
                Log.d(TAG,"scanning time passed! stoping scan");
                scanner.stopScan(leScanCallback);
                textViewPrinter.setText("done scanning");
            }
        }, SCAN_PERIOD);



//        if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
//                checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
        Log.d(TAG,"start scanning");
        textViewPrinter.setText("start scanning");
        scanner.startScan(leScanCallback);
    }

    private String[] cheakPermissionRequired(){ // return the not available premissions
        List<String> premissionRequired =new ArrayList<>();

//        if(checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)==PackageManager.PERMISSION_DENIED ){
//            premissionRequired.add(Manifest.permission.BLUETOOTH_CONNECT);
//        }
//        if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN)==PackageManager.PERMISSION_DENIED){
//            premissionRequired.add(Manifest.permission.BLUETOOTH_SCAN);
//        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_DENIED){
            //Log.d(TAG,"fine location not granted");
            premissionRequired.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        String print=" this is the premission list: ";
        String[] names=new String[premissionRequired.size()];
        for (int i=0;i<premissionRequired.size();i++){
            names[i]=premissionRequired.get(i);
            print= print+" "+premissionRequired.get(i);
        }

        Log.d(TAG,print);

        return names;
    }
    private static final int REQUEST_PERMISSION = 1; // serial idenify of the premission request
    private void sendRequestToUser(){
        String[] request=cheakPermissionRequired();

        if(request.length>0){
            Log.d(TAG,"reqesting permmition");
            requestPermissions(request,REQUEST_PERMISSION);
        }else {
            Log.d(TAG, "all premistion granted");
            scanLeDevice();
        }


    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allGranted=true;
        if (requestCode==REQUEST_PERMISSION){
            String print=" ";
            int i=0;
            for (String p:permissions){ // looping all the result to cheak if premission deniyed permently.
                if (!shouldShowRequestPermissionRationale(p) && grantResults[i]==-1) {
                    print= p;
                    Log.e(TAG, p+"--Permissions denied permanently. Directing user to settings.--");
                }
                i++;
            }
            for (int result:grantResults){
                if (result<0){
                    allGranted=false; //this is critical i only "//" this becuse of the phone i use
                    break;
                }

            }
            if (!allGranted){
                Log.d(TAG,"not all premission allowed -- can not scan");
            }else{
                Log.d(TAG,"all premission allowed starting sacnning...");
//                if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
//                        checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
                scanLeDevice();

            }
        }


    }
    boolean BleSettingsAvailable(){
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