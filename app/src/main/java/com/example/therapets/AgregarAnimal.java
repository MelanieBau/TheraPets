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

    private Uri fotoAnimal = null;
    private Uri fotoTerapeuta = null;
    private static final int PICK_FOTO_ANIMAL = 100;
    private static final int PICK_FOTO_TERAPEUTA = 101;
    private String centroId;
    private String fotoAnimalUrl = "";
    private String fotoTerapeutaUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_animal);

        centroId = getIntent().getStringExtra("centroId");

        ImageView ivFotoAnimal = findViewById(R.id.ivFotoAnimal);
        ImageView ivFotoTerapeuta = findViewById(R.id.ivFotoTerapeuta);
        Button btnFotoAnimal = findViewById(R.id.btnSeleccionarFotoAnimal);
        Button btnFotoTerapeuta = findViewById(R.id.btnSeleccionarFotoTerapeuta);
        TextInputEditText nombre = findViewById(R.id.etNombreAnimal);
        TextInputEditText tipo = findViewById(R.id.etTipoAnimal);
        TextInputEditText raza = findViewById(R.id.etRazaAnimal);
        TextInputEditText edad = findViewById(R.id.etEdadAnimal);
        TextInputEditText especialidad = findViewById(R.id.etEspecialidadAnimal);
        TextInputEditText nombreTerapeuta = findViewById(R.id.etNombreTerapeuta);
        TextInputEditText especialidadTerapeuta = findViewById(R.id.etEspecialidadTerapeuta);
        Button btnGuardar = findViewById(R.id.btnGuardarAnimal);

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        // Abrir galería para foto del animal
        btnFotoAnimal.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_FOTO_ANIMAL);
        });

        // Abrir galería para foto de la terapeuta
        btnFotoTerapeuta.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_FOTO_TERAPEUTA);
        });

        // Guardar animal
        btnGuardar.setOnClickListener(v -> {
            String nombreStr = nombre.getText().toString().trim();
            String tipoStr = tipo.getText().toString().trim();
            String razaStr = raza.getText().toString().trim();
            String edadStr = edad.getText().toString().trim();
            String especialidadStr = especialidad.getText().toString().trim();
            String nombreTerapeutaStr = nombreTerapeuta.getText().toString().trim();
            String especialidadTerapeutaStr = especialidadTerapeuta.getText().toString().trim();

            if (nombreStr.isEmpty()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            // Subimos primero la foto del animal si existe
            if (fotoAnimal != null) {
                subirFotoAnimal(nombreStr, tipoStr, razaStr, edadStr,
                        especialidadStr, nombreTerapeutaStr, especialidadTerapeutaStr);
            } else {
                // Si no hay foto del animal subimos la de la terapeuta directamente
                if (fotoTerapeuta != null) {
                    subirFotoTerapeuta(nombreStr, tipoStr, razaStr, edadStr,
                            especialidadStr, nombreTerapeutaStr, especialidadTerapeutaStr, "");
                } else {
                    guardarAnimal(nombreStr, tipoStr, razaStr, edadStr,
                            especialidadStr, "", nombreTerapeutaStr, especialidadTerapeutaStr, "");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == PICK_FOTO_ANIMAL) {
                fotoAnimal = data.getData();
                ((ImageView) findViewById(R.id.ivFotoAnimal)).setImageURI(fotoAnimal);
            } else if (requestCode == PICK_FOTO_TERAPEUTA) {
                fotoTerapeuta = data.getData();
                ((ImageView) findViewById(R.id.ivFotoTerapeuta)).setImageURI(fotoTerapeuta);
            }
        }
    }

    // Subimos primero la foto del animal
    private void subirFotoAnimal(String nombre, String tipo, String raza, String edad,
                                 String especialidad, String nombreTerapeuta, String especialidadTerapeuta) {
        Toast.makeText(this, "Subiendo foto...", Toast.LENGTH_SHORT).show();
        MediaManager.get().upload(fotoAnimal)
                .callback(new UploadCallback() {
                    @Override public void onStart(String requestId) {}
                    @Override public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String url = resultData.get("secure_url").toString();
                        // Luego subimos la foto de la terapeuta si existe
                        if (fotoTerapeuta != null) {
                            subirFotoTerapeuta(nombre, tipo, raza, edad,
                                    especialidad, nombreTerapeuta, especialidadTerapeuta, url);
                        } else {
                            guardarAnimal(nombre, tipo, raza, edad,
                                    especialidad, url, nombreTerapeuta, especialidadTerapeuta, "");
                        }
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(AgregarAnimal.this, "Error al subir foto", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }

    // Subimos la foto de la terapeuta
    private void subirFotoTerapeuta(String nombre, String tipo, String raza, String edad,
                                    String especialidad, String nombreTerapeuta,
                                    String especialidadTerapeuta, String fotoAnimalUrl) {
        MediaManager.get().upload(fotoTerapeuta)
                .callback(new UploadCallback() {
                    @Override public void onStart(String requestId) {}
                    @Override public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String url = resultData.get("secure_url").toString();
                        guardarAnimal(nombre, tipo, raza, edad,
                                especialidad, fotoAnimalUrl, nombreTerapeuta, especialidadTerapeuta, url);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(AgregarAnimal.this, "Error al subir foto terapeuta", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }

    // Guardamos todo en Firestore
    private void guardarAnimal(String nombre, String tipo, String raza, String edad,
                               String especialidad, String fotoUrl, String nombreTerapeuta,
                               String especialidadTerapeuta, String fotoTerapeutaUrl) {
        Map<String, Object> animal = new HashMap<>();
        animal.put("nombre", nombre);
        animal.put("tipo", tipo);
        animal.put("raza", raza);
        animal.put("edad", edad);
        animal.put("especialidad", especialidad);
        animal.put("centro", centroId);
        animal.put("fotoUrl", fotoUrl);
        animal.put("nombreTerapeuta", nombreTerapeuta);
        animal.put("especialidadTerapeuta", especialidadTerapeuta);
        animal.put("fotoTerapeuta", fotoTerapeutaUrl);

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