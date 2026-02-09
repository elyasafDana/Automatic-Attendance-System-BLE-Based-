package com.example.adplusscan;

import android.util.Log;

import com.google.gson.JsonObject;

public class TestFile {
    private static String TeacherUuid="asd";
    private static String lessonId="26";

    public static String getTeacherUuid(){
        return TeacherUuid;
    }
    public static String getLessonId(){
        return lessonId;
    }

    public static void forLoginTeacherTest(String lessonId,String uuid){
        bleViewModle bleView=new bleViewModle();
        JsonObject data=new JsonObject();
        data.addProperty("teacher_id",uuid);
        data.addProperty("date",bleView.getDate());
        bleView.updateValue("lessons?lesson_id=eq."+lessonId,data,isvalid->{
            if (isvalid){
                Log.d("asd","data was posted to the supabase");
            }else {
                Log.d("asd","data was not posted to the supabase");
            }
        });
    }

    public static void forLoginStudentTest(String lessonId,String uuid){
        Log.d("asd","this is the test file");
        JsonObject data=new JsonObject();
        data.addProperty("lesson_id",lessonId);
        data.addProperty("student_id",uuid);
        data.addProperty("status", false);
        bleViewModle bleView=new bleViewModle();
        bleView.postValue("student_on_lesson",data,isvalid->{
            if (isvalid){
                Log.d("asd","data from test was posted to the supabase");

            }else {
                Log.d("asd","data from test was not posted to the supabase");
            }

        });


    }
}
