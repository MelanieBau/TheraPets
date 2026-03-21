package com.example.therapets;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        CardView cardCentros = findViewById(R.id.cardCentros);
        CardView cardAnimales = findViewById(R.id.cardAnimales);
        CardView cardCitas = findViewById(R.id.cardCitas);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesionAdmin);

        // Ir a gestionar centros
        cardCentros.setOnClickListener(v -> {
            startActivity(new Intent(this, GestionCentrosActivity.class));
        });

        // Ir a gestionar animales
        cardAnimales.setOnClickListener(v -> {
            startActivity(new Intent(this, GestionAnimalesActivity.class));
        });

        // Ir a ver todas las citas
        cardCitas.setOnClickListener(v -> {
            startActivity(new Intent(this, GestionCitasActivity.class));
        });

        // Cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, IniciarSesion.class));
            finish();
        });
    }
}