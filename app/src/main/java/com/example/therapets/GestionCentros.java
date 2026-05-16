package com.example.therapets;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class GestionCentros extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<Centro> listaCentros;
    private CentroAdapter adapter;
    private boolean soloMiCentro;
    private String centroId;
    private Toast toastActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_centros);

        db = FirebaseFirestore.getInstance();

        soloMiCentro = getIntent().getBooleanExtra("soloMiCentro", false);
        TextView tvTitulo = findViewById(R.id.tvTituloGestionCentros);

        if (soloMiCentro) {
            tvTitulo.setText("Mi Centro");
        } else {
            tvTitulo.setText("Gestionar Centros");
        }
        centroId = getIntent().getStringExtra("centroId");

        RecyclerView rvCentros = findViewById(R.id.rvCentros);
        Button btnAgregar = findViewById(R.id.btnAgregarCentro);

        if (soloMiCentro) {
            btnAgregar.setVisibility(View.GONE);
        }

        rvCentros.setLayoutManager(new LinearLayoutManager(this));
        listaCentros = new ArrayList<>();
        adapter = new CentroAdapter(listaCentros, this::mostrarDialogoEditar, this::borrarCentro);
        rvCentros.setAdapter(adapter);

        cargarCentros();

        btnAgregar.setOnClickListener(v ->
                startActivity(new Intent(this, AgregarCentro.class)));
    }

    private void cargarCentros() {
        if (soloMiCentro && centroId != null) {
            db.collection("centros").whereEqualTo("nombre", centroId).get().addOnSuccessListener(queryDocumentSnapshots -> {
                listaCentros.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Centro centro = doc.toObject(Centro.class);
                    centro.setId(doc.getId());
                    listaCentros.add(centro);
                }
                adapter.notifyDataSetChanged();
            });
        } else {
            db.collection("centros").get().addOnSuccessListener(queryDocumentSnapshots -> {
                listaCentros.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Centro centro = doc.toObject(Centro.class);
                    centro.setId(doc.getId());
                    listaCentros.add(centro);
                }
                adapter.notifyDataSetChanged();
            });
        }
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

            db.collection("centros").document(centro.getId()).update(datos).addOnSuccessListener(a -> cargarCentros());
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void borrarCentro(Centro centro) {
        if (soloMiCentro) {
            mostrarToast("No tienes permisos para borrar el centro");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Borrar centro")
                .setMessage("¿Estás segura de que quieres borrar " + centro.getNombre() + "? Se eliminarán también todas sus citas, animales y horarios.")
                .setPositiveButton("Borrar", (dialog, which) -> borrarCentroYRelacionados(centro))
                .setNegativeButton("Cancelar", null).show();
    }

    private void borrarCentroYRelacionados(Centro centro) {
        String nombreCentro = centro.getNombre();

        // Borramos las citas del centro
        db.collection("citas").whereEqualTo("centro", nombreCentro).get().addOnSuccessListener(snap -> {
            for (QueryDocumentSnapshot doc : snap) {
                doc.getReference().delete();
            }
        });

        // Borramos los animales del centro
        db.collection("animales").whereEqualTo("centro", nombreCentro).get().addOnSuccessListener(snap -> {
            for (QueryDocumentSnapshot doc : snap) {
                doc.getReference().delete();
            }
        });

        // Borramos los horarios del centro
        db.collection("horarios").whereEqualTo("centroId", nombreCentro).get().addOnSuccessListener(snap -> {
            for (QueryDocumentSnapshot doc : snap) {
                doc.getReference().delete();
            }
        });

        // Borramos el centro
        db.collection("centros").document(centro.getId()).delete().addOnSuccessListener(a -> cargarCentros());
    }

    private void mostrarToast(String mensaje) {
        if (toastActual != null) toastActual.cancel();
        toastActual = Toast.makeText(this, mensaje, Toast.LENGTH_SHORT);
        toastActual.show();
    }
}