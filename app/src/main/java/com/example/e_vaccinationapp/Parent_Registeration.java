package com.example.e_vaccinationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Parent_Registeration extends AppCompatActivity {

    private EditText et_full_name, et_email, et_phone, et_password, et_confirm_password, et_cnic;
    private String fullname, email, phone, cnic, password, confirm_password, name_pattern, cnic_pattern, email_pattern, password_pattern, phone_pattern;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_registeration);

        et_full_name = findViewById(R.id.et_full_name);
        et_email = findViewById(R.id.et_email);
        et_phone = findViewById(R.id.et_phone);
        et_cnic = findViewById(R.id.et_employee_id);
        et_password = findViewById(R.id.et_password);
        et_confirm_password = findViewById(R.id.et_confirm_password);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        name_pattern = "^[a-zA-Z].*[\\s\\.]*$";
        email_pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        password_pattern = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=.])"
                + "(?=\\S+$).{8,20}$";
        phone_pattern = "^((\\+92)|(0092))-{0,1}\\d{3}-{0,1}\\d{7}$|^\\d{11}$|^\\d{4}-\\d{7}$";
        cnic_pattern = "^[0-9+]{5}-[0-9+]{7}-[0-9]{1}$";
        progressBar = findViewById(R.id.progressBar_register);
    }

    public void validate_signup() {

        fullname = et_full_name.getText().toString();
        email = et_email.getText().toString();
        phone = et_phone.getText().toString();
        cnic = et_cnic.getText().toString();
        password = et_password.getText().toString();
        confirm_password = et_confirm_password.getText().toString();

        if (TextUtils.isEmpty(fullname)) {
            et_full_name.setError("full name is required");
        }
        if (TextUtils.isEmpty(email)) {
            et_email.setError("email is required");
        }
        if (TextUtils.isEmpty(phone)) {
            et_phone.setError("phone is required");
        }
        if (TextUtils.isEmpty(cnic)) {
            et_cnic.setError("cnic is required");
        }
        if (TextUtils.isEmpty(password)) {
            et_password.setError("password is required");
        }
        if (TextUtils.isEmpty(confirm_password)) {
            et_confirm_password.setError("confirm password is required");
        }
        if (!TextUtils.isEmpty(fullname) && !fullname.trim().matches(name_pattern)) {
            et_full_name.setError("invalid name");
        }
        if (!TextUtils.isEmpty(cnic) && !cnic.trim().matches(cnic_pattern)) {
            et_cnic.setError("invalid name");
        }
        if (!TextUtils.isEmpty(email) && !email.trim().matches(email_pattern)) {
            et_email.setError("invalid email address");
        }
        if (!password.equals(confirm_password)) {
            et_confirm_password.setError("password does not match");
        }
        if (!TextUtils.isEmpty(password) && !password.trim().matches(password_pattern)) {

            et_password.setError("password must contain atleast one [a-z],[A-Z],[0-9],[@#$%^&+=.]");
        }
        if (!TextUtils.isEmpty(phone) && !phone.trim().matches(phone_pattern)) {
            et_phone.setError("not valid phone number");
        }

        if (!TextUtils.isEmpty(fullname) && !TextUtils.isEmpty(cnic) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(confirm_password) && email.trim().matches(email_pattern) && password.equals(confirm_password)
                && password.trim().matches(password_pattern) && phone.trim().matches(phone_pattern) && cnic.trim().matches(cnic_pattern)
                && fullname.trim().matches(name_pattern)) {

            progressBar.setVisibility(View.VISIBLE);
            register_user(fullname, phone, cnic, email, password, "user_parent");
        }
    }

    public void register_user(final String full_name, final String phone_, final String cnic, final String email_, final String password_, final String user_type) {

        firestore.collection("users").whereEqualTo("phone", phone_).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.getResult().isEmpty()) {

                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        CollectionReference users = firestore.collection("users");
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("username", full_name);
                                        user.put("phone", phone_);
                                        user.put("email", email_);
                                        user.put("cnic", cnic);
                                        user.put("password", password_);
                                        user.put("user_type", user_type);
                                        users.document(email).set(user);
                                        Toast.makeText(getApplicationContext(), "user successfully created", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);

                                        startActivity(new Intent(getApplicationContext(), Login.class));
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "email already exist", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "phone already exist", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    public void btn_signup_click(View view) {

        validate_signup();
    }
}