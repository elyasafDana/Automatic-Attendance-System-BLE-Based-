package com.example.adplusscan;


import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class Student {
    // שדות המחלקה בהתאם לעמודות ב-Supabase
    private String  uuid;
    private String first_name;
    private String last_name;

    // בנאי ריק - חובה עבור ספריות כמו Gson ו-Supabase
    public Student() {
    }

    // בנאי מלא לנוחות העבודה בקוד
    public Student(String  uuid, String first_name, String last_name) {
        this.uuid = uuid;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    // --- Getters ו-Setters ---

    public String getuuid() {
        return uuid;
    }

    public void setuuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
            json.addProperty("student_id", uuid);
            json.addProperty("first_name", first_name);
            json.addProperty("last_name", last_name);

        return json;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
}
