package com.example.e_vaccinationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class HW_Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hw_dashboard);
    }

    public void btn_profile(View view) {

        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
    }

    public void btn_notifications(View view) {

        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
    }

    public void btn_register_new_baby(View view) {

        startActivity(new Intent(this, Baby_Registrarion_HW.class));
    }

    public void btn_shedule(View view) {

        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
    }

    public void btn_upcomming_vaccinations(View view) {

        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
    }

    public void btn_registered_babies(View view) {

        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
    }
}