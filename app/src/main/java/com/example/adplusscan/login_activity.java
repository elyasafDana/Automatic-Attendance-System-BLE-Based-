package com.example.adplusscan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import io.github.jan.supabase.SupabaseClient;

public class login_activity extends AppCompatActivity {
    private TextView tvTitle;
    private EditText etEmail;
    private EditText etPassword;
    private TextView tvTeacherLogin;
    private Button btnRegister;
    private SignInButton googleRegister;
    private GoogleSignInClient GoogleSignInClient;
    private SupabaseClient supabase;
    //private String table;
    private String uuid;
    private String type = "student";
    private Intent intent;
    String idToken;
    private ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), // זה ה"חוזה" - אנחנו מצפים לתוצאה מ-Activity
            result -> {
                // זה הקוד שירוץ כשהמשתמש יסיים את הבחירה בגוגל
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        idToken = account.getIdToken(); // זה "כרטיס הכניסה"
                        loginView.loginWithSupabase(idToken, type, info -> {
                            uuid = info.get("uuid").getAsString();
                            long timeDiff = info.get("timeDiff").getAsLong();
                            if (timeDiff < 5000) {//if this is the first logging  we need to upload the details.
                                Intent intent = new Intent(login_activity.this, detailes_activity.class);
                                intent.putExtra("uuid", uuid);
                                intent.putExtra("idToken", idToken);
                                intent.putExtra("type", type);
                                intent.putExtra("timeDiff", timeDiff);
                                startActivity(intent);

                            } else {
                                supabaseRepository supaRepo = supabaseRepository.singeltonBuilder();
                                supaRepo.getInfo("/" + type + "?" + type + "_id" + "=eq." + uuid, jsonResponse -> { // if this isnt the first time i want to cheack if you logged in in the right type
                                    if (jsonResponse.size() == 0) {
                                        Log.d("asd", "has no " + type);
                                        activateIntent(type, true);

                                    } else {
                                        Log.d("asd", "i found a " + type + jsonResponse.toString());
                                        activateIntent(type, false);
                                    }
                                });
                            }

//                            Intent intent = new Intent(login_activity.this, detailes_activity.class);
//                            intent.putExtra("uuid",uuid);
//                            intent.putExtra("idToken",idToken);
//                            intent.putExtra("type",type);
//                            intent.putExtra("timeDiff",timeDiff);
//                            startActivity(intent);

//
//                            Log.d("asd","the uuid is: " +uuid);
//
//
//                            supabaseRepository supaRepo=supabaseRepository.singeltonBuilder();
//                            if (timeDiff<5000){
//                                Log.d("asd","this is the first time the user signed in");
//                                activateIntent(type,false);
//                            }
//                            else {
//                                supaRepo.getInfo("/"+type+"?"+type+"_id"+"=eq."+uuid,jsonResponse->{ // if this isnt the first time i want to cheack if you logged in in the right type
//                                    if (jsonResponse.size()==0){
//                                        Log.d("asd","has no "+type);
//                                        activateIntent(type,true);
//
//                                    }else {
//                                        Log.d("asd","i found a "+ type+ jsonResponse.toString());
//                                        activateIntent(type,false);
//                                    }
//                                });
//                            }
                        });
                        // supabaseRepository.loginWithSupabase(idToken);
                    } catch (ApiException e) {
                        Log.d("asd", "something went wrong in login " + e.toString());
                        throw new RuntimeException(e);
                    }
                } else {
                    Log.d("asd", "something went wrong " + result.toString());
                }
            }
    );


    private void activateIntent(String type, boolean isOpositeIntent) {
        if (isOpositeIntent) {
            if (type.equals("teacher")) {
                intent = new Intent(login_activity.this, teacher_main_activity.class);
            } else {
                intent = new Intent(login_activity.this, student_main_activity.class);
            }
        } else {
            if (type.equals("teacher")) {
                intent = new Intent(login_activity.this, teacher_main_activity.class);
            } else {
                intent = new Intent(login_activity.this, student_main_activity.class);
            }
        }
        intent.putExtra("uuid", uuid);
        intent.putExtra("idToken", idToken);
        intent.putExtra("type", type);
        Log.d("asd", "i got from  uuid: " + uuid);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        setting();


        // הגדרת אפשרויות ההתחברות של גוגל
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("367177611225-q71jdhv56v437p9eja28sf0kv6glt4b9.apps.googleusercontent.com")
                .requestEmail()
                .build();


        GoogleSignInClient = GoogleSignIn.getClient(this, gso);


        googleRegister.setOnClickListener(v -> {
            Log.d("sad", "trying to sign in");
            sighIn();

        });

        tvTeacherLogin.setOnClickListener(v -> {
            if (type.equals("teacher")) {
                type = "student";
                tvTitle.setText("welcome " + type);
            } else {
                type = "teacher";
                tvTitle.setText("welcome " + type);
            }

        });

    }


    private void setting() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        googleRegister = findViewById(R.id.btnGoogleSignIn);
        tvTeacherLogin = findViewById(R.id.tvTeacherLogin);
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("welcome " + type);


    }

    private void sighIn() {
        Intent signInIntent = GoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);

    }


}
