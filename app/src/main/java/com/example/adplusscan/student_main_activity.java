package com.example.adplusscan;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


/*
THIS IS VERY IMPORTENT MSG!!!
you need uuid from the login intent only for the teacher- cus he needs to upload it as a passsword
 */

public class student_main_activity extends AppCompatActivity {
    private static final int GPS_REQUEST_CODE = 1001;
    BluetoothLeScanner scanner;
    private TextView textViewPrinter;
    private static final String TAG = "asd";
    private static final long SCAN_PERIOD = 10000;
    private String uuid;
    private bleViewModle bleView=new bleViewModle();

    private supabaseRepository supabaseRepo= supabaseRepository.singeltonBuilder();
    private  String phoneId;
    private String idToken;
    private GPSViewModel gpsView;
    ActivityResultLauncher<Intent> bluethoothLuncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
            , result->{
        if (result.getResultCode()==RESULT_OK){
            Log.d(TAG,"user opened bluethooth");
        }
        else{
            Log.d(TAG,"bluetooth not enabled");
        }

    });

    ActivityResultLauncher<Intent> GpsLuncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result->{
        if (result.getResultCode()==RESULT_OK){
            Log.d(TAG,"user opened Gps");
        }
        else{
            Log.d(TAG,"Gps not enabled");
        }
    });

    ActivityResultLauncher<String[]> requestPermissionLuncherForScanner=
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),result->{
                boolean allGranted=true;
                for (String p:result.keySet()){
                    if (result.get(p)){
                        Log.d(TAG,p+" granted");
                    }else{
                        allGranted=false;
                        Log.d(TAG,p+" not granted");
                        return;
                    }
                }
                Log.d(TAG," all premission granted");
                String studentId=uuid;
                String date=bleView.getDate();

                 bleView.scanLeDevice(studentId);

            }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_main);

        uuid=getIntent().getStringExtra("uuid");
        idToken=getIntent().getStringExtra("idToken");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button ScannerButton = findViewById(R.id.scan);
        textViewPrinter = findViewById(R.id.statusText);
        textViewPrinter.setText("");

         gpsView = new GPSViewModel();



        ScannerButton.setOnClickListener(v -> {
            if (bleView.isScannerOn()){
                textViewPrinter.setText("already scanning");
                Log.d("asd","already scanning");
                return;
            }
            if (!bleView.isBleAvailable()){
                Log.d("asd","not all settings good");
                Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                bluethoothLuncher.launch(intent);
                return;
            }
            scanner= BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
            gpsView.checkGPS(this);


            //scanner= BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
            //requestPermissionLuncherForScanner.launch(bleView.cheakPermissionRequired(this));
        });

        bleView.getuploadedDone().observe(this,status->{
            if (status){
                textViewPrinter.setText("you registered to the class");
            }else {
                textViewPrinter.setText("you are not in the class...");
            }
        });

        gpsView.getShowEnableGPSDialog().observe(this, show -> {
            if (Boolean.TRUE.equals(show)) {
                gpsView.requestEnableGPS(this, GPS_REQUEST_CODE);
            }
        });

        gpsView.getGpsEnabled().observe(this, enabled -> {
            if (Boolean.TRUE.equals(enabled)) {
                requestPermissionLuncherForScanner.launch(bleView.cheakPermissionRequired(this));
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // המשתמש אישר → GPS הופעל
                gpsView.checkGPS(this); // עדכון ה-state
            }
        }
    }




}