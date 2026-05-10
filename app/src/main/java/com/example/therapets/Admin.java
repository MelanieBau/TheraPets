package com.example.therapets;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;

public class Admin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //Tarjeta de centros
        CardView cardCentros = findViewById(R.id.cardCentros);

        //Tarjeta de los coordinadores
        CardView cardCoordinadores = findViewById(R.id.cardAnimales);

        //Tarjetas de todas las citas de TODOS los centros
        CardView cardCitas = findViewById(R.id.cardCitas);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesionAdmin);

        // Ir a tarjeta gestionar centros
        cardCentros.setOnClickListener(v -> {
            startActivity(new Intent(this, GestionCentros.class));
        });

        // Ir a tarjeta gestionar coordinadores
        cardCoordinadores.setOnClickListener(v ->
                startActivity(new Intent(this, GestionCoordinadores.class)));

        // Ir a todas las citas
        cardCitas.setOnClickListener(v -> {
            startActivity(new Intent(this, GestionCitas.class));
        });

        // Cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, IniciarSesion.class));
            finish();
        });
    }
}