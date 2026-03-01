package com.example.therapets;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AgendarCitaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_cita);

        if (savedInstanceState == null) {
            CitaDraftStore.clear();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.stepContainer, new Paso1DatosFragment())
                    .commit();
        }
    }
}