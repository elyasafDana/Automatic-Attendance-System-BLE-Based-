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

import com.google.gson.JsonObject;

import java.util.UUID;

public class teacher_main_activity extends AppCompatActivity {  BluetoothLeScanner scanner;
    private TextView textViewPrinter;
    private static final String TAG = "asd";
    private static final long SCAN_PERIOD = 10000;
    private String uuid;
    private bleViewModle bleView=new bleViewModle();
    private GPSViewModel gpsView;
    private static final int GPS_REQUEST_CODE = 1001;

    private supabaseRepository supabaseRepo= supabaseRepository.singeltonBuilder();
    private  String phoneId;
    private String idToken;
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


    ActivityResultLauncher<String[]> requestPermissionLuncherForAdvetiser=registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),result->{

                //uuid=TestFile.getTeacherUuid();

                bleView.startAd( uuid,isvalid-> {
                    if (isvalid){
                        Log.d("asd", "data was posted to the supabase");
                    }else {
                        Log.d("asd", "data was not posted to the supabase");
                    }
                });
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_main);

        uuid=getIntent().getStringExtra("uuid");
        idToken=getIntent().getStringExtra("idToken");
        Log.d("asd","msg from main activity :uuid: "+uuid);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        gpsView = new GPSViewModel();




        Button AdButton= findViewById(R.id.ad);


        AdButton.setOnClickListener(V->{
            if (bleView.isAdvertising()){
                Log.d("asd","already advertising");
                return;
            }
            if (!bleView.isBleAvailable()){
                Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                bluethoothLuncher.launch(intent);
                Log.d("asd","not al settings good");
                return;
            }
            gpsView.checkGPS(this);

        });

        gpsView.getShowEnableGPSDialog().observe(this, show -> {
            if (Boolean.TRUE.equals(show)) {
                gpsView.requestEnableGPS(this, GPS_REQUEST_CODE);
            }
        });

        gpsView.getGpsEnabled().observe(this, enabled -> {
            if (Boolean.TRUE.equals(enabled)) {
                requestPermissionLuncherForAdvetiser.launch(bleView.cheakPermissionRequired(this));
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