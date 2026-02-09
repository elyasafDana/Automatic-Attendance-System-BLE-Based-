package com.example.adplusscan;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class loginView {
    private static String TAG="asd";
    private static   supabaseRepository supaBase=supabaseRepository.singeltonBuilder();;

    public  static  void loginWithSupabase(String idToken, String table, Consumer<JsonObject> callback){
        supaBase.loginWithSupabase(idToken,table,callback);
    }

//    public static boolean BleSettingsAvailable(){
//        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
//        if(bluetoothAdapter==null){
//            Log.d(TAG,"you have no bluetooth device on your phone!!");
//            return false;
//        }
//
//        if (!bluetoothAdapter.isEnabled()){
//            Log.d(TAG,"please enable bluetooth!!!!!");
////            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
////            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT_CODE);
//            return false;
//        }
//
//        if(!bluetoothAdapter.isMultipleAdvertisementSupported()){
//            Log.d(TAG,"your phone does not support BLE!!");
//            return false;
//        }
//        Log.d(TAG,"all bluetooth settings are good");
//        return true;
//    }











}
