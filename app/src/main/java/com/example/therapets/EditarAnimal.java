package com.example.therapets;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EditarAnimal extends AppCompatActivity {

    private Uri fotoAnimal = null;
    private Uri fotoTerapeuta = null;
    private static final int PICK_FOTO_ANIMAL = 100;
    private static final int PICK_FOTO_TERAPEUTA = 101;
    private String animalId;
    private String fotoAnimalActual;
    private String fotoTerapeutaActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_animal);

        // Recibimos todos los datos del animal
        animalId = getIntent().getStringExtra("animalId");
        String nombre = getIntent().getStringExtra("nombre");
        String tipo = getIntent().getStringExtra("tipo");
        String raza = getIntent().getStringExtra("raza");
        String edad = getIntent().getStringExtra("edad");
        String especialidad = getIntent().getStringExtra("especialidad");
        fotoAnimalActual = getIntent().getStringExtra("fotoUrl");
        String nombreTerapeuta = getIntent().getStringExtra("nombreTerapeuta");
        String especialidadTerapeuta = getIntent().getStringExtra("especialidadTerapeuta");
        fotoTerapeutaActual = getIntent().getStringExtra("fotoTerapeuta");

        ImageView ivFotoAnimal = findViewById(R.id.ivFotoAnimalEditar);
        ImageView ivFotoTerapeuta = findViewById(R.id.ivFotoTerapeutaEditar);
        Button btnFotoAnimal = findViewById(R.id.btnSeleccionarFotoEditar);
        Button btnFotoTerapeuta = findViewById(R.id.btnSeleccionarFotoTerapeutaEditar);
        TextInputEditText etNombre = findViewById(R.id.etNombreAnimalEditar);
        TextInputEditText etTipo = findViewById(R.id.etTipoAnimalEditar);
        TextInputEditText etRaza = findViewById(R.id.etRazaAnimalEditar);
        TextInputEditText etEdad = findViewById(R.id.etEdadAnimalEditar);
        TextInputEditText etEspecialidad = findViewById(R.id.etEspecialidadAnimalEditar);
        TextInputEditText etNombreTerapeuta = findViewById(R.id.etNombreTerapeutaEditar);
        TextInputEditText etEspecialidadTerapeuta = findViewById(R.id.etEspecialidadTerapeutaEditar);
        Button btnGuardar = findViewById(R.id.btnGuardarAnimalEditar);

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        // Rellenamos los datos actuales
        etNombre.setText(nombre);
        etTipo.setText(tipo);
        etRaza.setText(raza);
        etEdad.setText(edad);
        etEspecialidad.setText(especialidad);
        etNombreTerapeuta.setText(nombreTerapeuta);
        etEspecialidadTerapeuta.setText(especialidadTerapeuta);

        // Mostramos las fotos actuales
        if (fotoAnimalActual != null && !fotoAnimalActual.isEmpty())
            Glide.with(this).load(fotoAnimalActual).centerCrop().into(ivFotoAnimal);

        if (fotoTerapeutaActual != null && !fotoTerapeutaActual.isEmpty())
            Glide.with(this).load(fotoTerapeutaActual).centerCrop().into(ivFotoTerapeuta);

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

        // Guardar cambios
        btnGuardar.setOnClickListener(v -> {
            String nuevoNombre = etNombre.getText().toString().trim();
            String nuevoTipo = etTipo.getText().toString().trim();
            String nuevaRaza = etRaza.getText().toString().trim();
            String nuevaEdad = etEdad.getText().toString().trim();
            String nuevaEspecialidad = etEspecialidad.getText().toString().trim();
            String nuevoNombreTerapeuta = etNombreTerapeuta.getText().toString().trim();
            String nuevaEspecialidadTerapeuta = etEspecialidadTerapeuta.getText().toString().trim();

            if (nuevoNombre.isEmpty()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            if (fotoAnimal != null) {
                subirFotoAnimal(nuevoNombre, nuevoTipo, nuevaRaza, nuevaEdad,
                        nuevaEspecialidad, nuevoNombreTerapeuta, nuevaEspecialidadTerapeuta);
            } else if (fotoTerapeuta != null) {
                subirFotoTerapeuta(nuevoNombre, nuevoTipo, nuevaRaza, nuevaEdad,
                        nuevaEspecialidad, nuevoNombreTerapeuta, nuevaEspecialidadTerapeuta, fotoAnimalActual);
            } else {
                guardarCambios(nuevoNombre, nuevoTipo, nuevaRaza, nuevaEdad, nuevaEspecialidad,
                        fotoAnimalActual, nuevoNombreTerapeuta, nuevaEspecialidadTerapeuta, fotoTerapeutaActual);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == PICK_FOTO_ANIMAL) {
                fotoAnimal = data.getData();
                ((ImageView) findViewById(R.id.ivFotoAnimalEditar)).setImageURI(fotoAnimal);
            } else if (requestCode == PICK_FOTO_TERAPEUTA) {
                fotoTerapeuta = data.getData();
                ((ImageView) findViewById(R.id.ivFotoTerapeutaEditar)).setImageURI(fotoTerapeuta);
            }
        }
    }

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
                        if (fotoTerapeuta != null) {
                            subirFotoTerapeuta(nombre, tipo, raza, edad,
                                    especialidad, nombreTerapeuta, especialidadTerapeuta, url);
                        } else {
                            guardarCambios(nombre, tipo, raza, edad, especialidad,
                                    url, nombreTerapeuta, especialidadTerapeuta, fotoTerapeutaActual);
                        }
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(EditarAnimal.this, "Error al subir foto", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }

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
                        guardarCambios(nombre, tipo, raza, edad, especialidad,
                                fotoAnimalUrl, nombreTerapeuta, especialidadTerapeuta, url);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(EditarAnimal.this, "Error al subir foto terapeuta", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }

    private void guardarCambios(String nombre, String tipo, String raza, String edad,
                                String especialidad, String fotoUrl, String nombreTerapeuta,
                                String especialidadTerapeuta, String fotoTerapeutaUrl) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("nombre", nombre);
        datos.put("tipo", tipo);
        datos.put("raza", raza);
        datos.put("edad", edad);
        datos.put("especialidad", especialidad);
        datos.put("fotoUrl", fotoUrl);
        datos.put("nombreTerapeuta", nombreTerapeuta);
        datos.put("especialidadTerapeuta", especialidadTerapeuta);
        datos.put("fotoTerapeuta", fotoTerapeutaUrl);

        FirebaseFirestore.getInstance()
                .collection("animales")
                .document(animalId)
                .update(datos)
                .addOnSuccessListener(a -> {
                    Toast.makeText(this, "Animal actualizado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show());
    }
}