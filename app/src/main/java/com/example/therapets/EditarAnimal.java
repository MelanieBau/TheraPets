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

    private Uri fotoSeleccionada = null;
    private static final int PICK_IMAGE = 100;
    private String animalId;
    private String fotoUrlActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_animal);

        // Recibimos los datos del animal a editar
        animalId = getIntent().getStringExtra("animalId");
        String nombre = getIntent().getStringExtra("nombre");
        String tipo = getIntent().getStringExtra("tipo");
        String raza = getIntent().getStringExtra("raza");
        String edad = getIntent().getStringExtra("edad");
        String especialidad = getIntent().getStringExtra("especialidad");
        fotoUrlActual = getIntent().getStringExtra("fotoUrl");

        ImageView ivFoto = findViewById(R.id.ivFotoAnimalEditar);
        Button btnFoto = findViewById(R.id.btnSeleccionarFotoEditar);
        TextInputEditText Nombre = findViewById(R.id.etNombreAnimalEditar);
        TextInputEditText Tipo = findViewById(R.id.etTipoAnimalEditar);
        TextInputEditText Raza = findViewById(R.id.etRazaAnimalEditar);
        TextInputEditText Edad = findViewById(R.id.etEdadAnimalEditar);
        TextInputEditText Especialidad = findViewById(R.id.etEspecialidadAnimalEditar);
        Button btnGuardar = findViewById(R.id.btnGuardarAnimalEditar);

        // Rellenamos los datos actuales
        Nombre.setText(nombre);
        Tipo.setText(tipo);
        Raza.setText(raza);
        Edad.setText(edad);
        Especialidad.setText(especialidad);

        // Mostramos la foto actual si existe
        if (fotoUrlActual != null && !fotoUrlActual.isEmpty()) {
            Glide.with(this).load(fotoUrlActual).centerCrop().into(ivFoto);
        }

        // Abrir galería
        btnFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        // Guardar cambios
        btnGuardar.setOnClickListener(v -> {
            String nuevoNombre = Nombre.getText().toString().trim();
            String nuevoTipo = Tipo.getText().toString().trim();
            String nuevaRaza = Raza.getText().toString().trim();
            String nuevaEdad = Edad.getText().toString().trim();
            String nuevaEspecialidad = Especialidad.getText().toString().trim();

            if (nuevoNombre.isEmpty()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            if (fotoSeleccionada != null) {
                subirFotoYGuardar(nuevoNombre, nuevoTipo, nuevaRaza, nuevaEdad, nuevaEspecialidad);
            } else {
                guardarCambios(nuevoNombre, nuevoTipo, nuevaRaza, nuevaEdad, nuevaEspecialidad, fotoUrlActual);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            fotoSeleccionada = data.getData();
            ImageView ivFoto = findViewById(R.id.ivFotoAnimalEditar);
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
                        String fotoUrl = resultData.get("secure_url").toString();
                        guardarCambios(nombre, tipo, raza, edad, especialidad, fotoUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(EditarAnimal.this, "Error al subir foto", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                })
                .dispatch();
    }

    private void guardarCambios(String nombre, String tipo, String raza, String edad, String especialidad, String fotoUrl) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("nombre", nombre);
        datos.put("tipo", tipo);
        datos.put("raza", raza);
        datos.put("edad", edad);
        datos.put("especialidad", especialidad);
        datos.put("fotoUrl", fotoUrl);

        FirebaseFirestore.getInstance()
                .collection("animales")
                .document(animalId)
                .update(datos)
                .addOnSuccessListener(a -> {
                    Toast.makeText(this, "Animal actualizado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
                });
    }
}