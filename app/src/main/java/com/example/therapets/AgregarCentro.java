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

public class AgregarCentro extends AppCompatActivity {

    private Uri fotoSeleccionada = null;
    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_centro);

        // Referencias a los elementos de la pantalla
        ImageView foto = findViewById(R.id.ivFotoCentro);
        Button btnFoto = findViewById(R.id.btnSeleccionarFotoCentro);
        TextInputEditText nombre = findViewById(R.id.etNombreCentro);
        TextInputEditText direccion = findViewById(R.id.etDireccionCentro);
        TextInputEditText telefono = findViewById(R.id.etTelefonoCentro);
        Button btnGuardar = findViewById(R.id.btnGuardarCentro);
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        // Abre la galería del móvil para elegir una foto
        btnFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        // Cuando pulsa guardar
        btnGuardar.setOnClickListener(v -> {
            String nombreStr = nombre.getText().toString().trim();
            String direccionStr = direccion.getText().toString().trim();
            String telefonoStr = telefono.getText().toString().trim();

            // El nombre es obligatorio
            if (nombreStr.isEmpty()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            // Si hay foto la subimos primero, si no guardamos sin foto
            if (fotoSeleccionada != null) {
                subirFotoYGuardar(nombreStr, direccionStr, telefonoStr);
            } else {
                guardarCentro(nombreStr, direccionStr, telefonoStr, "");
            }
        });
    }

    // Recibimos la foto elegida de la galería
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            fotoSeleccionada = data.getData();
            ImageView foto = findViewById(R.id.ivFotoCentro);
            foto.setImageURI(fotoSeleccionada);
        }
    }

    // Subimos la foto a Cloudinary y luego guardamos el centro
    private void subirFotoYGuardar(String nombre, String direccion, String telefono) {
        Toast.makeText(this, "Subiendo foto...", Toast.LENGTH_SHORT).show();
        MediaManager.get().upload(fotoSeleccionada)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        // Foto subida correctamente, guardamos el centro con su URL
                        guardarCentro(nombre, direccion, telefono, resultData.get("secure_url").toString());
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(AgregarCentro.this, "Error al subir foto", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                })
                .dispatch();
    }

    // Guardamos el centro en Firestore con todos sus datos
    private void guardarCentro(String nombre, String direccion, String telefono, String fotoUrl) {
        Map<String, Object> centro = new HashMap<>();
        centro.put("nombre", nombre);
        centro.put("direccion", direccion);
        centro.put("telefono", telefono);
        centro.put("fotoUrl", fotoUrl);

        FirebaseFirestore.getInstance()
                .collection("centros")
                .add(centro)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, "Centro guardado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show());
    }
}