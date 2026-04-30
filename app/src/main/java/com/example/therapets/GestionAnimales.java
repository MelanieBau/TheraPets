package com.example.therapets;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

public class GestionAnimales extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<Animal> listaAnimales;
    private AnimalAdapter adapter;
    private String centroId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_animales);

        db = FirebaseFirestore.getInstance();

        RecyclerView rvAnimales = findViewById(R.id.rvAnimales);
        Button btnAgregar = findViewById(R.id.btnAgregarAnimal);
        rvAnimales.setLayoutManager(new LinearLayoutManager(this));
        listaAnimales = new ArrayList<>();
        adapter = new AnimalAdapter(listaAnimales, this::mostrarDialogoEditar, this::borrarAnimal);
        rvAnimales.setAdapter(adapter);

        // Obtenemos el centroId del coordinador logueado
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(document -> {
                    centroId = document.getString("centroId");
                    cargarAnimales();
                });

        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(this, AgregarAnimal.class);
            intent.putExtra("centroId", centroId);
            startActivity(intent);
        });
    }

    private void cargarAnimales() {
        // Solo cargamos los animales del centro del coordinador
        db.collection("animales")
                .whereEqualTo("centro", centroId)
                .get()
                .addOnSuccessListener(snap -> {
                    listaAnimales.clear();
                    for (QueryDocumentSnapshot doc : snap) {
                        Animal animal = doc.toObject(Animal.class);
                        animal.setId(doc.getId());
                        listaAnimales.add(animal);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void mostrarDialogoEditar(Animal animal) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_animal, null);

        TextInputEditText etNombre = dialogView.findViewById(R.id.etNombreAnimal);
        TextInputEditText etTipo = dialogView.findViewById(R.id.etTipoAnimal);

        etNombre.setText(animal.getNombre());
        etTipo.setText(animal.getTipo());

        new AlertDialog.Builder(this)
                .setTitle("Editar animal")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("nombre", etNombre.getText().toString().trim());
                    datos.put("tipo", etTipo.getText().toString().trim());
                    datos.put("centro", centroId); // Centro automático

                    db.collection("animales").document(animal.getId())
                            .update(datos)
                            .addOnSuccessListener(a -> {
                                Toast.makeText(this, "Animal actualizado", Toast.LENGTH_SHORT).show();
                                cargarAnimales();
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void borrarAnimal(Animal animal) {
        new AlertDialog.Builder(this)
                .setTitle("Borrar animal")
                .setMessage("¿Borrar a " + animal.getNombre() + "?")
                .setPositiveButton("Borrar", (dialog, which) ->
                        db.collection("animales").document(animal.getId())
                                .delete()
                                .addOnSuccessListener(a -> {
                                    Toast.makeText(this, "Animal eliminado", Toast.LENGTH_SHORT).show();
                                    cargarAnimales();
                                }))
                .setNegativeButton("Cancelar", null)
                .show();
    }
}