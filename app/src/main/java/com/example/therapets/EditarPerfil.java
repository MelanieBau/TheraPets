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

    private Toast toastActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil);

        TextInputEditText nombre = findViewById(R.id.etNombreEditar);
        TextInputEditText telefono = findViewById(R.id.etTelefonoEditar);
        Button btnGuardar = findViewById(R.id.btnGuardarPerfil);

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("usuarios").document(uid).get().addOnSuccessListener(document -> {
            nombre.setText(document.getString("nombre"));
            telefono.setText(document.getString("telefono"));
        });

        btnGuardar.setOnClickListener(v -> {
            String nombreStr = nombre.getText().toString().trim();
            String telefonoStr = telefono.getText().toString().trim();

            if (nombreStr.isEmpty()) {
                mostrarToast("El nombre es obligatorio");
                return;
            }

            Map<String, Object> datos = new HashMap<>();
            datos.put("nombre", nombreStr);
            datos.put("telefono", telefonoStr);

            FirebaseFirestore.getInstance().collection("usuarios").document(uid).update(datos).addOnSuccessListener(a -> {
                mostrarToast("Perfil actualizado");
                finish();
            }).addOnFailureListener(e -> mostrarToast("Error al guardar"));
        });
    }

    private void mostrarToast(String mensaje) {
        if (toastActual != null) toastActual.cancel();
        toastActual = Toast.makeText(this, mensaje, Toast.LENGTH_SHORT);
        toastActual.show();
    }
}