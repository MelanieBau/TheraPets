package com.example.therapets;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AgregarAnimal extends AppCompatActivity {

    private Uri fotoSeleccionada = null;
    private static final int PICK_IMAGE = 100;
    private String centroId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_animal);

        centroId = getIntent().getStringExtra("centroId");

        ImageView ivFoto = findViewById(R.id.ivFotoAnimal);
        Button btnFoto = findViewById(R.id.btnSeleccionarFotoAnimal);
        TextInputEditText nombre = findViewById(R.id.etNombreAnimal);
        TextInputEditText tipo = findViewById(R.id.etTipoAnimal);
        TextInputEditText raza = findViewById(R.id.etRazaAnimal);
        TextInputEditText edad = findViewById(R.id.etEdadAnimal);
        TextInputEditText especialidad = findViewById(R.id.etEspecialidadAnimal);
        Button btnGuardar = findViewById(R.id.btnGuardarAnimal);

        // Abrir galería
        btnFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        // Guardar animal
        btnGuardar.setOnClickListener(v -> {
            String nombreStr = nombre.getText().toString().trim();
            String tipoStr = tipo.getText().toString().trim();
            String razaStr = raza.getText().toString().trim();
            String edadStr = edad.getText().toString().trim();
            String especialidadStr = especialidad.getText().toString().trim();

            if (nombreStr.isEmpty()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            if (fotoSeleccionada != null) {
                subirFotoYGuardar(nombreStr, tipoStr, razaStr, edadStr, especialidadStr);
            } else {
                guardarAnimal(nombreStr, tipoStr, razaStr, edadStr, especialidadStr, "");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            fotoSeleccionada = data.getData();
            ImageView ivFoto = findViewById(R.id.ivFotoAnimal);
            ivFoto.setImageURI(fotoSeleccionada);
        }
    }

    private void subirFotoYGuardar(String nombre, String tipo, String raza, String edad, String especialidad) {
        Toast.makeText(this, "Subiendo foto...", Toast.LENGTH_SHORT).show();
        MediaManager.get().upload(fotoSeleccionada)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        guardarAnimal(nombre, tipo, raza, edad, especialidad, resultData.get("secure_url").toString());
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(AgregarAnimal.this, "Error al subir foto", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                })
                .dispatch();
    }

    private void guardarAnimal(String nombre, String tipo, String raza, String edad, String especialidad, String fotoUrl) {
        Map<String, Object> animal = new HashMap<>();
        animal.put("nombre", nombre);
        animal.put("tipo", tipo);
        animal.put("raza", raza);
        animal.put("edad", edad);
        animal.put("especialidad", especialidad);
        animal.put("centro", centroId);
        animal.put("fotoUrl", fotoUrl);

        FirebaseFirestore.getInstance()
                .collection("animales")
                .add(animal)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, "Animal guardado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show());
    }
}