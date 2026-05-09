package com.example.therapets;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class HistoriaTherapets extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historia_therapets);

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
    }
}