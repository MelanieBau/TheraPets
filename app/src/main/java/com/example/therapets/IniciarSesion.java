package com.example.therapets;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class IniciarSesion extends AppCompatActivity {

    @Override
        protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        TextView tvOlvidocontraseña = findViewById(R.id.tvOlvidarcontraseña);
        tvOlvidocontraseña.setOnClickListener(v -> {
            Toast.makeText(this, "Proximamente: recuperar la contraseña", Toast.LENGTH_SHORT).show();
        });
    }
}