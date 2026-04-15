package com.example.therapets;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class CoordinadorActivity extends AppCompatActivity {

    private String centroId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinador);

        TextView tvNombre = findViewById(R.id.tvNombreCoordinador);
        CardView cardMiCentro = findViewById(R.id.cardMiCentro);
        CardView cardMisAnimales = findViewById(R.id.cardMisAnimales);
        CardView cardCitasCentro = findViewById(R.id.cardCitasCentro);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesionCoordinador);

        // Obtener datos del coordinador desde Firestore
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String nombre = document.getString("nombre");
                        centroId = document.getString("centroId");
                        tvNombre.setText("Hola, " + nombre);
                    }
                });

        // Ir a gestionar su centro
        cardMiCentro.setOnClickListener(v -> {
            Intent intent = new Intent(this, GestionCentrosActivity.class);
            intent.putExtra("centroId", centroId);
            intent.putExtra("soloMiCentro", true);
            startActivity(intent);
        });

        // Ir a gestionar sus animales
        cardMisAnimales.setOnClickListener(v -> {
            Intent intent = new Intent(this, GestionAnimalesActivity.class);
            intent.putExtra("centroId", centroId);
            intent.putExtra("soloMiCentro", true);
            startActivity(intent);
        });

        // Cuando el coordinador pulsa "Citas de mi Centro"
        // va a su pantalla específica con botones de confirmar y cancelar
        cardCitasCentro.setOnClickListener(v -> {
            startActivity(new Intent(this, GestionCitasCoordinadorActivity.class));
        });

        // Cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, IniciarSesion.class));
            finish();
        });
    }
}