package com.example.therapets;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView logo = findViewById(R.id.logoImageTherapets);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        // Logo aparece con fade simple
        logo.setAlpha(0f);
        logo.animate().alpha(1f).setDuration(600).start();

        // Animamos la barra de progreso
        Handler handler = new Handler(Looper.getMainLooper());
        int[] progress = {0};

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (progress[0] <= 100) {
                    progressBar.setProgress(progress[0]);
                    progress[0] += 2;
                    handler.postDelayed(this, 30);
                } else {
                    // Cuando llega al 100% va a la siguiente pantalla
                    startActivity(new Intent(MainActivity.this, BienvenidoTherapets.class));
                    finish();
                }
            }
        };

        handler.postDelayed(runnable, 600);
    }
}