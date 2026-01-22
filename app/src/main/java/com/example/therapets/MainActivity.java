package com.example.therapets;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Pantalla clikeable.
        findViewById(R.id.main).setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity.this, BienvenidoTherapetsActivity.class);

            //Abrir pestaña BienvenidoTheraPets.
            startActivity(intent);
            finish();
        });
    }
}