package com.example.therapets;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EditarPerfil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil);

        TextInputEditText nombre = findViewById(R.id.etNombreEditar);
        TextInputEditText telefono = findViewById(R.id.etTelefonoEditar);
        Button btnGuardar = findViewById(R.id.btnGuardarPerfil);

        //Boton volver
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Cargamos los datos actuales del usuario
        FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    nombre.setText(document.getString("nombre"));
                    telefono.setText(document.getString("telefono"));
                });

        // Guardamos los cambios en Firestore
        btnGuardar.setOnClickListener(v -> {
            String nombreStr = nombre.getText().toString().trim();
            String telefonoStr = telefono.getText().toString().trim();

            if (nombreStr.isEmpty()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> datos = new HashMap<>();
            datos.put("nombre", nombreStr);
            datos.put("telefono", telefonoStr);

            FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(uid)
                    .update(datos)
                    .addOnSuccessListener(a -> {
                        Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show());
        });
    }
}