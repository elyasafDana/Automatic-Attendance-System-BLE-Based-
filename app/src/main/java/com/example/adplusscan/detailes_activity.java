package com.example.adplusscan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.function.Consumer;

public class detailes_activity extends AppCompatActivity {

    private EditText firstName;
    EditText lastName;
    String firstNameString;
    String lastNameString;
    Button save;
    private static String uuid;
    private static String idToken;
    private static String type;
    private static int timeDiff;
    private Intent intent;
    private supabaseRepository supaRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detailes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        settings();


        save.setOnClickListener(v -> {
            firstNameString = firstName.getText().toString();
            lastNameString = lastName.getText().toString();

            uploadTypeClassToSupa(info -> {
                activateIntent(type, false);
            });
        });
    }

    private void uploadTypeClassToSupa(Consumer<Boolean> info) {

        Log.d("asd", "first time the user signed in");
        String lessonId = TestFile.getLessonId();
        if (type.equals("student")) {
            Student student = new Student(uuid, firstNameString, lastNameString);
            supaRepo.postValue(type, student.toJsonObject(), callback -> {
                TestFile.forLoginStudentTest(lessonId, uuid);
                info.accept(true);
            });
            //TestFile.forLoginStudentTest(lessonId,uuid);
        } else {
            Teacher teacher = new Teacher(uuid, firstNameString, lastNameString);
            supaRepo.postValue(type, teacher.toJsonObject(), callback -> {
                TestFile.forLoginTeacherTest(lessonId, uuid);
                info.accept(true);
            });

        }

    }

    private void activateIntent(String type, boolean isOpositeIntent) {
        if (isOpositeIntent) {
            if (type.equals("teacher")) {
                intent = new Intent(detailes_activity.this, teacher_main_activity.class);
            } else {
                intent = new Intent(detailes_activity.this, student_main_activity.class);
            }
        } else {
            if (type.equals("teacher")) {
                intent = new Intent(detailes_activity.this, teacher_main_activity.class);
            } else {
                intent = new Intent(detailes_activity.this, student_main_activity.class);
            }
        }
        intent.putExtra("uuid", uuid);
        intent.putExtra("idToken", idToken);
        intent.putExtra("type", type);
        Log.d("asd", "i got from  uuid: " + uuid);
        startActivity(intent);
        finish();
    }

    public void settings() {
        supaRepo = supabaseRepository.singeltonBuilder();
        firstName = findViewById(R.id.etFirstName);
        lastName = findViewById(R.id.etLastName);
        save = findViewById(R.id.btnSave);

        uuid = getIntent().getStringExtra("uuid");
        idToken = getIntent().getStringExtra("idToken");
        type = getIntent().getStringExtra("type");
        timeDiff = getIntent().getIntExtra("timeDiff", 5000);


    }
}