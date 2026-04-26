package com.example.therapets;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionCoordinadoresActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<Usuario> listaCoordinadores;
    private CoordinadorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_coordinadores);

        // Inicializamos Firestore
        db = FirebaseFirestore.getInstance();

        // Configuramos el RecyclerView
        RecyclerView rvCoordinadores = findViewById(R.id.rvCoordinadores);
        Button btnAgregar = findViewById(R.id.btnAgregarCoordinador);
        rvCoordinadores.setLayoutManager(new LinearLayoutManager(this));

        // Inicializamos la lista y el adapter
        listaCoordinadores = new ArrayList<>();
        adapter = new CoordinadorAdapter(listaCoordinadores, this::mostrarDialogoBorrar);
        rvCoordinadores.setAdapter(adapter);

        // Cargamos los coordinadores existentes
        cargarCoordinadores();

        // Cuando el admin pulsa añadir coordinador
        btnAgregar.setOnClickListener(v -> mostrarDialogoAgregar());
    }

    private void cargarCoordinadores() {
        // Buscamos en Firestore todos los usuarios con rol coordinador
        db.collection("usuarios")
                .whereEqualTo("rol", "coordinador")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaCoordinadores.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Usuario usuario = doc.toObject(Usuario.class);
                        usuario.setId(doc.getId());
                        listaCoordinadores.add(usuario);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar coordinadores", Toast.LENGTH_SHORT).show();
                });
    }

    private void mostrarDialogoAgregar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Añadir coordinador");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_coordinador, null);
        builder.setView(dialogView);

        // Referencias a los campos del dialog
        TextInputEditText etNombre = dialogView.findViewById(R.id.etNombreCoordinador);
        TextInputEditText etEmail = dialogView.findViewById(R.id.etEmailCoordinador);
        TextInputEditText etPassword = dialogView.findViewById(R.id.etPasswordCoordinador);
        Spinner spCentro = dialogView.findViewById(R.id.spCentroCoordinador);

        // Cargamos los centros en el spinner
        List<String> centros = new ArrayList<>();
        centros.add("Selecciona un centro");
        db.collection("centros")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        centros.add(doc.getString("nombre"));
                    }
                    ArrayAdapter<String> adapterCentros = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_dropdown_item, centros);
                    spCentro.setAdapter(adapterCentros);
                });

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String centro = spCentro.getSelectedItem().toString();

            // Validamos los campos
            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (centro.equals("Selecciona un centro")) {
                Toast.makeText(this, "Selecciona un centro", Toast.LENGTH_SHORT).show();
                return;
            }

            // Creamos el coordinador en Firebase Authentication
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        String uid = authResult.getUser().getUid();

                        // Guardamos los datos del coordinador en Firestore
                        Map<String, Object> coordinador = new HashMap<>();
                        coordinador.put("nombre", nombre);
                        coordinador.put("email", email);
                        coordinador.put("rol", "coordinador");
                        coordinador.put("centroId", centro);

                        db.collection("usuarios")
                                .document(uid)
                                .set(coordinador)
                                .addOnSuccessListener(a -> {
                                    Toast.makeText(this, "Coordinador creado correctamente", Toast.LENGTH_SHORT).show();
                                    cargarCoordinadores();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarDialogoBorrar(Usuario usuario) {
        // Confirmamos antes de borrar
        new AlertDialog.Builder(this)
                .setTitle("Borrar coordinador")
                .setMessage("¿Estás segura de que quieres borrar a " + usuario.getNombre() + "?")
                .setPositiveButton("Borrar", (dialog, which) -> {
                    // Borramos el documento de Firestore
                    db.collection("usuarios")
                            .document(usuario.getId())
                            .delete()
                            .addOnSuccessListener(a -> {
                                Toast.makeText(this, "Coordinador eliminado", Toast.LENGTH_SHORT).show();
                                cargarCoordinadores();
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}