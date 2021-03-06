package com.example.e_vaccinationapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Login extends AppCompatActivity implements LocationListener {

    private EditText et_email,et_password;
    private String password, email;
    private String type="";
    private FirebaseAuth fauth;
    private FirebaseFirestore firestore;
    private String phone_pattern,email_pattern;
    private DocumentReference documentReference;
    private ProgressBar progressBar;
    private DatabaseReference mDatabase;
    private static boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_email = findViewById(R.id.et_email2);
        et_password = findViewById(R.id.et_password2);
        fauth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        phone_pattern = "^((\\+92)|(0092))-{0,1}\\d{3}-{0,1}\\d{7}$|^\\d{11}$|^\\d{4}-\\d{7}$";
        email_pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        progressBar = findViewById(R.id.progressBar_login);

        startLocationManager();
        checkSession();
    }

    private void checkSession() {

        //check if user is logged in
        //if user is logged in --> move to mainActivity

        SessionManagement sessionManagement = new SessionManagement(Login.this);
        int userID = sessionManagement.getSession();

        if(userID != -1){
            //user id logged in and so move to mainActivity
            moveToMainActivity();
        }
        else{
            //do nothing
        }
    }

    private void moveToMainActivity() {


        Intent intent = new Intent(Login.this, HW_Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void startLocationManager() {

        if(isLocationEnabled()) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference("Realtime_Tracking/Users");
    }

    public void btn_login(View view) {

        validate_login();
    }

    private void validate_login() {

        email = et_email.getText().toString();
        password = et_password.getText().toString();
        if(TextUtils.isEmpty(email)){

            et_email.setError("email is required");
        }
        if(TextUtils.isEmpty(password)){

            et_password.setError("password is required");
        }

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            progressBar.setVisibility(View.VISIBLE);
            if (email.matches(email_pattern)) {

                log_in_user(email);
            }
            else
            if(email.matches(phone_pattern)) {


                firestore.collection("users").whereEqualTo("phone", email).get().addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {

                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (!task.getResult().isEmpty()) {

                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        email = document.getId();
                                    }
                                    log_in_user(email);
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Invalid Username Or Password", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
            }
            else {
                Toast.makeText(getApplicationContext(), "Invalid Username Or Password", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void log_in_user(final String email) {

        fauth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    documentReference = firestore.collection("users").document(email);
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            DocumentSnapshot document = task.getResult();
                            type = document.getString("user_type");

                            if (type.equals("user_parent") && Start.flag) {

                                progressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(getApplicationContext(), User_Dashboard.class));
                            }
                            else
                            if (type.equals("health_worker") && !Start.flag) {

                                flag = true;
                                progressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(getApplicationContext(), HW_Dashboard.class));
                            }
                            else {

                                if(!type.isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "Invalid Username Or Password", Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(getApplicationContext(), "Invalid Username Or Password", Toast.LENGTH_SHORT).show();
                    type = "";
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void btn_back_singin(View view) {

        Intent intent = new Intent(this, Start.class);
        startActivity(intent);
    }

    public void btn_signup(View view) {

        if(Start.flag){

            startActivity(new Intent(this, Parent_Registeration.class));
        }
        else{
            startActivity(new Intent(this, Health_Worker_Registration.class));
        }
    }

    public void btn_forgot_password(View view) {

        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onLocationChanged(@NonNull final Location location) {

        if(flag){

            documentReference = firestore.collection("users").document(email);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    DocumentSnapshot document = task.getResult();
                    String name = document.getString("username");
                    String phone = document.getString("phone");
                    mDatabase.child(name+" "+phone).child("Lat").setValue(location.getLatitude());
                    mDatabase.child(name+" "+phone).child("Lng").setValue(location.getLongitude());
                }
            });
        }
    }
}