package com.example.therapets;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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

public class AgregarAnimalActivity extends AppCompatActivity {

    private Uri fotoSeleccionada = null;
    private static final int PICK_IMAGE = 100;
    private String centroId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_animal);

        // Recibimos el centroId del coordinador automáticamente
        centroId = getIntent().getStringExtra("centroId");

        ImageView ivFoto = findViewById(R.id.ivFotoAnimal);
        Button btnFoto = findViewById(R.id.btnSeleccionarFotoAnimal);
        TextInputEditText etNombre = findViewById(R.id.etNombreAnimal);
        TextInputEditText etTipo = findViewById(R.id.etTipoAnimal);
        Button btnGuardar = findViewById(R.id.btnGuardarAnimal);

        // Ocultamos el campo centro porque se asigna automáticamente
        findViewById(R.id.etCentroAnimal).setVisibility(View.GONE);

        // Abrir galería
        btnFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        // Guardar animal
        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String tipo = etTipo.getText().toString().trim();

            if (nombre.isEmpty()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            if (fotoSeleccionada != null) {
                subirFotoYGuardar(nombre, tipo);
            } else {
                guardarAnimal(nombre, tipo, "");
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

    private void subirFotoYGuardar(String nombre, String tipo) {
        Toast.makeText(this, "Subiendo foto...", Toast.LENGTH_SHORT).show();

        MediaManager.get().upload(fotoSeleccionada)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String fotoUrl = resultData.get("secure_url").toString();
                        guardarAnimal(nombre, tipo, fotoUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(AgregarAnimalActivity.this, "Error al subir foto", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                })
                .dispatch();
    }

    private void guardarAnimal(String nombre, String tipo, String fotoUrl) {
        Map<String, Object> animal = new HashMap<>();
        animal.put("nombre", nombre);
        animal.put("tipo", tipo);
        animal.put("centro", centroId); // Centro automático del coordinador
        animal.put("fotoUrl", fotoUrl);

        FirebaseFirestore.getInstance()
                .collection("animales")
                .add(animal)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, "Animal guardado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
                });
    }
}