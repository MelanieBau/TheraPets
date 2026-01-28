package com.example.therapets;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

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

        ImageView logo = findViewById(R.id.logoImageTherapets);
        TextView titulo = findViewById(R.id.textoInicio);


        //Estado inicial del logo (invisible)
        logo.setAlpha(0f);
        logo.setScaleX(85f);
        logo.setScaleY(85f);


        //Estado inicial del titulo (invisible)
        titulo.setAlpha(0f);
        titulo.setTranslationY(12f);

        //Animación del logo (fade + zoom suave)
        logo.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(700).setStartDelay(150).start();

        //Animación del texto
        titulo.animate().alpha(1f).translationY(0f).setDuration(600).setStartDelay(400).start();

        //Cambiar al Bienvenido luego de la animación

        new android.os.Handler(android.os.Looper.getMainLooper())
                 .postDelayed(() -> {
                     startActivity(new Intent(
                             MainActivity.this, BienvenidoTherapetsActivity.class
                     ));
                     finish();
                 }, 1800);
    }
}