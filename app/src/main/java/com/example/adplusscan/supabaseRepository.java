package com.example.adplusscan;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class supabaseRepository {
    private static final Logger log = LoggerFactory.getLogger(supabaseRepository.class);
    public static supabaseRepository instance;
    private  OkHttpClient client;
    private  Gson gson ;
    private static String uuid;
    private supabaseRepository(){
        client=new OkHttpClient();
        gson=new Gson();
    }
    public static supabaseRepository singeltonBuilder(){
        if (instance==null){
            instance=new supabaseRepository();
        }
        return instance;
    }
    public void getInfo(String table,Consumer<JsonArray> callback) {
        Request request = new Request.Builder()
                .url(supabaseConfig.SUPABASE_URL+table)///student_on_lessons?select=lesson_id,student(first_name),lessons(date,course_id(course_name))&student_id=eq.10001"
                .addHeader("apikey", supabaseConfig.SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + supabaseConfig.SUPABASE_KEY)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("failed: " + e.getMessage());
                Log.d("asd","FAILED " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String rawData = response.body().string();
                    JsonArray jsonResponse = JsonParser.parseString(rawData).getAsJsonArray();
                    callback.accept(jsonResponse);

                }
                else {
                    String errorJson = response.body().string();
                    Log.e("asd", "Supabase Error: " + errorJson);
                }
            }
        });
    }
    public void updateValue(String table, JsonObject data, Consumer<Boolean> callback){
        RequestBody body = RequestBody.create(data.toString(), okhttp3.MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(supabaseConfig.SUPABASE_URL+"/"+table)
                .patch(body)
                .addHeader("apikey", supabaseConfig.SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + supabaseConfig.SUPABASE_KEY)
                .addHeader("Content-Type", "application/json")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.accept(false);
                Log.e("asd", "Update failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.accept(true);
                    Log.d("asd", "data was posted to the supabase");
                } else {
                    Log.e("asd","server error" + response.body().string());
                }
            }
        });


    }

    public  void postValue(String table, JsonObject data,Consumer<Boolean> callback){
        RequestBody body = RequestBody.create(data.toString(), okhttp3.MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(supabaseConfig.SUPABASE_URL+"/"+table)
                .post(body)
                .addHeader("apikey", supabaseConfig.SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + supabaseConfig.SUPABASE_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "resolution=merge-duplicates")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("asd", "Update failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.accept(true);
                    Log.d("asd", "data was posted to the supabase");
                } else {
                    callback.accept(false);
                    Log.e("asd","server error" + response.body().string());
                }
            }
        });


    }
    private static long calculateDiff(JsonObject user){
        Instant createdAt = Instant.parse(user.get("created_at").getAsString());
        Instant lastSignInAt = Instant.parse(user.get("last_sign_in_at").getAsString());
        long createdMillis = createdAt.toEpochMilli();
        long loginMillis = lastSignInAt.toEpochMilli();
        return Math.abs(loginMillis - createdMillis);
    }
    public static void loginWithSupabase(String idToken, String table, Consumer<JsonObject> callback ) {
        OkHttpClient client = new OkHttpClient();

        JsonObject json=new JsonObject();
        json.addProperty("provider", "google");
        json.addProperty("id_token", idToken);
        Log.d("asd","going to create JSON");



       RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("https://nszdtpgpbrawdbooctcz.supabase.co/auth/v1/token?grant_type=id_token")
                .addHeader("apikey",supabaseConfig.SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + supabaseConfig.SUPABASE_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("asd", "failed: " +  e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("asd","you in supabase with google");
                    String responseBody = response.body().string();
                    uuid="";
                    JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                    JsonObject user = jsonObject.get("user").getAsJsonObject();
                    uuid = user.get("id").getAsString();
                    String acssesToken=jsonObject.get("access_token").getAsString();
                    Log.d("asd","the access token is"+acssesToken);
                    long diff=calculateDiff(user);
                    Log.d("asd","this is the diff "+String.valueOf(diff));
                    supabaseRepository supa=new supabaseRepository();
//                    if (diff < 5000) { //first time the user signed in
//                        Log.d("asd", "first time the user signed in");
//
//                        String lessonId=TestFile.getLessonId();;
//                        if (table.equals("student")){
//                            Student student=new Student(uuid,"Ali","cohen");
//                            supa.postValue(table,student.toJsonObject(),callback->{
//                                TestFile.forLoginStudentTest(lessonId,uuid);
//                            });
//                            //TestFile.forLoginStudentTest(lessonId,uuid);
//                        }else{
//                            Teacher teacher=new Teacher(uuid,"Ali","cohen");
//                            supa.postValue(table,teacher.toJsonObject(),callback->{
//                                TestFile.forLoginTeacherTest(lessonId,uuid);
//                            });
//
//                        }
//                     }
//                    else{
//                        Log.d("asd", "user signed in before");
//                    }
                    JsonObject data=new JsonObject();
                    data.addProperty("uuid",uuid);
                    data.addProperty("timeDiff",diff);
                    callback.accept(data);

                }
            }
        });

    }

//    private static void forLoginStudentTest(String lessonId,String uuid){
//        JsonObject data=new JsonObject();
//        data.addProperty("lesson_id",lessonId);
//        data.addProperty("student_id",uuid);
//        bleViewModle bleView=new bleViewModle();
//        bleView.postValue("student_on_lesson",data,isvalid->{
//
//        });
//
//
//    }

//    private static void forLoginTeacherTest(String lessonId,String uuid){
//        bleViewModle bleView=new bleViewModle();
//        JsonObject data=new JsonObject();
//        data.addProperty("teacher_id",uuid);
//        data.addProperty("date",bleView.getDate());
//        bleView.updateValue("lessons?lesson_id=eq."+lessonId,data,isvalid->{
//        });
//    }



}
