package com.example.therapets;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionCentrosActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<Centro> listaCentros;
    private CentroAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_centros);

        db = FirebaseFirestore.getInstance();

        RecyclerView rvCentros = findViewById(R.id.rvCentros);
        Button btnAgregar = findViewById(R.id.btnAgregarCentro);

        rvCentros.setLayoutManager(new LinearLayoutManager(this));
        listaCentros = new ArrayList<>();
        adapter = new CentroAdapter(listaCentros, this::mostrarDialogoEditar, this::borrarCentro);
        rvCentros.setAdapter(adapter);

        cargarCentros();

        btnAgregar.setOnClickListener(v -> mostrarDialogoAgregar());
    }

    private void cargarCentros() {
        db.collection("centros")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaCentros.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Centro centro = doc.toObject(Centro.class);
                        centro.setId(doc.getId());
                        listaCentros.add(centro);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void mostrarDialogoAgregar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Añadir centro");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_centro, null);
        builder.setView(dialogView);

        EditText etNombre = dialogView.findViewById(R.id.etNombreCentro);
        EditText etDireccion = dialogView.findViewById(R.id.etDireccionCentro);
        EditText etTelefono = dialogView.findViewById(R.id.etTelefonoCentro);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre = etNombre.getText().toString().trim();
            String direccion = etDireccion.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();

            if (nombre.isEmpty()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> centro = new HashMap<>();
            centro.put("nombre", nombre);
            centro.put("direccion", direccion);
            centro.put("telefono", telefono);

            db.collection("centros")
                    .add(centro)
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(this, "Centro añadido", Toast.LENGTH_SHORT).show();
                        cargarCentros();
                    });
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarDialogoEditar(Centro centro) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar centro");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_centro, null);
        builder.setView(dialogView);

        EditText etNombre = dialogView.findViewById(R.id.etNombreCentro);
        EditText etDireccion = dialogView.findViewById(R.id.etDireccionCentro);
        EditText etTelefono = dialogView.findViewById(R.id.etTelefonoCentro);

        etNombre.setText(centro.getNombre());
        etDireccion.setText(centro.getDireccion());
        etTelefono.setText(centro.getTelefono());

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            Map<String, Object> datos = new HashMap<>();
            datos.put("nombre", etNombre.getText().toString().trim());
            datos.put("direccion", etDireccion.getText().toString().trim());
            datos.put("telefono", etTelefono.getText().toString().trim());

            db.collection("centros")
                    .document(centro.getId())
                    .update(datos)
                    .addOnSuccessListener(a -> {
                        Toast.makeText(this, "Centro actualizado", Toast.LENGTH_SHORT).show();
                        cargarCentros();
                    });
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void borrarCentro(Centro centro) {
        new AlertDialog.Builder(this)
                .setTitle("Borrar centro")
                .setMessage("¿Estás segura de que quieres borrar " + centro.getNombre() + "?")
                .setPositiveButton("Borrar", (dialog, which) -> {
                    db.collection("centros")
                            .document(centro.getId())
                            .delete()
                            .addOnSuccessListener(a -> {
                                Toast.makeText(this, "Centro eliminado", Toast.LENGTH_SHORT).show();
                                cargarCentros();
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}