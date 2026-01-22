package com.example.therapets;

//Paquetes
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BienvenidoTherapetsActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bienvenido_therapets);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Botones del Layout
        Button botonContinuar = findViewById(R.id.btnContinuar);
        Button botonRegistrarme = findViewById(R.id.btnRegistrarme);


        //Boton continuar a pantalla IniciarSesion
        botonContinuar.setOnClickListener(v -> {
            Intent intent = new Intent(BienvenidoTherapetsActivity.this, IniciarSesion.class);
            startActivity(intent);
        });

        //Boton para registrarse en pantalla registro
        botonRegistrarme.setOnClickListener(v -> {
            Intent intent = new Intent(BienvenidoTherapetsActivity.this, RegistroTheraPets.class);
            startActivity(intent);
        });
    }
}