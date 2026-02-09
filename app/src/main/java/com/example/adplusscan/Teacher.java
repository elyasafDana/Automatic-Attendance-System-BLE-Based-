package com.example.adplusscan;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class Teacher {
    // שדות שתואמים לטבלה שלך
    private String id_teacher;
    private String first_name;
    private String last_name;
    private String phone_id;

    public Teacher() {}

    public Teacher(String id_teacher, String first_name, String last_name/*, String phone_id*/ ){
        this.id_teacher = id_teacher;
        this.first_name = first_name;
        this.last_name = last_name;
        //this.phone_id = phone_id;
    }

    // זו הפונקציה שחשובה לך - היא מחזירה אובייקט מסוג JSONObject
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject(); // יצירת מופע של JSONObject
        json.addProperty("teacher_id", this.id_teacher);
        json.addProperty("first_name", this.first_name);
        json.addProperty("last_name", this.last_name);
       // json.addProperty("phone_id", this.phone_id);
        return json;
    }

    // Getters & Setters
    public String getId_teacher() { return id_teacher; }
    public void setId_teacher(String id_teacher) { this.id_teacher = id_teacher; }

    public String getFirst_name() { return first_name; }
    public void setFirst_name(String first_name) { this.first_name = first_name; }

    public String getLast_name() { return last_name; }
    public void setLast_name(String last_name) { this.last_name = last_name; }



//    public String getPhone_id() { return phone_id; }
//    public void setPhone_id(String phone_id) { this.phone_id = phone_id; }
}