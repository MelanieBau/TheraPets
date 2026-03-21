package com.example.therapets;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionAnimalesActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<Animal> listaAnimales;
    private AnimalAdapter adapter;

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

        cargarAnimales();

        btnAgregar.setOnClickListener(v -> mostrarDialogoAgregar());
    }

    private void cargarAnimales() {
        db.collection("animales")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaAnimales.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Animal animal = doc.toObject(Animal.class);
                        animal.setId(doc.getId());
                        listaAnimales.add(animal);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void mostrarDialogoAgregar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Añadir animal");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_animal, null);
        builder.setView(dialogView);

        TextInputEditText etNombre = dialogView.findViewById(R.id.etNombreAnimal);
        TextInputEditText etTipo = dialogView.findViewById(R.id.etTipoAnimal);
        TextInputEditText etCentro = dialogView.findViewById(R.id.etCentroAnimal);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre = etNombre.getText().toString().trim();
            String tipo = etTipo.getText().toString().trim();
            String centro = etCentro.getText().toString().trim();

            if (nombre.isEmpty()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> animal = new HashMap<>();
            animal.put("nombre", nombre);
            animal.put("tipo", tipo);
            animal.put("centro", centro);

            db.collection("animales")
                    .add(animal)
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(this, "Animal añadido", Toast.LENGTH_SHORT).show();
                        cargarAnimales();
                    });
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarDialogoEditar(Animal animal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar animal");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_animal, null);
        builder.setView(dialogView);

        TextInputEditText etNombre = dialogView.findViewById(R.id.etNombreAnimal);
        TextInputEditText etTipo = dialogView.findViewById(R.id.etTipoAnimal);
        TextInputEditText etCentro = dialogView.findViewById(R.id.etCentroAnimal);

        etNombre.setText(animal.getNombre());
        etTipo.setText(animal.getTipo());
        etCentro.setText(animal.getCentro());

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            Map<String, Object> datos = new HashMap<>();
            datos.put("nombre", etNombre.getText().toString().trim());
            datos.put("tipo", etTipo.getText().toString().trim());
            datos.put("centro", etCentro.getText().toString().trim());

            db.collection("animales")
                    .document(animal.getId())
                    .update(datos)
                    .addOnSuccessListener(a -> {
                        Toast.makeText(this, "Animal actualizado", Toast.LENGTH_SHORT).show();
                        cargarAnimales();
                    });
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void borrarAnimal(Animal animal) {
        new AlertDialog.Builder(this)
                .setTitle("Borrar animal")
                .setMessage("¿Estás segura de que quieres borrar " + animal.getNombre() + "?")
                .setPositiveButton("Borrar", (dialog, which) -> {
                    db.collection("animales")
                            .document(animal.getId())
                            .delete()
                            .addOnSuccessListener(a -> {
                                Toast.makeText(this, "Animal eliminado", Toast.LENGTH_SHORT).show();
                                cargarAnimales();
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
