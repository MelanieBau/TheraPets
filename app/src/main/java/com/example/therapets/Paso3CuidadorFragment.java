package com.example.therapets;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class Paso3CuidadorFragment extends Fragment {

    public Paso3CuidadorFragment() {
        super(R.layout.fragment_paso3_cuidador);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv = view.findViewById(R.id.rvAnimalesSeleccion);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<Animal> listaAnimales = new ArrayList<>();

        // Cuando el usuario selecciona un animal
        AnimalSeleccion seleccion = new AnimalSeleccion(listaAnimales, animal -> {
            // Guardamos el animal elegido y vamos al Paso 4
            CitaDraftStore.cuidador = animal.getNombre() + " (" + animal.getTipo() + ")";
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.stepContainer, new Paso4MotivoFragment())
                    .addToBackStack(null)
                    .commit();
        });

        rv.setAdapter(seleccion);

        // Cargamos los animales del centro elegido en el Paso 2
        FirebaseFirestore.getInstance()
                .collection("animales")
                .whereEqualTo("centro", CitaDraftStore.centro)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaAnimales.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Animal animal = doc.toObject(Animal.class);
                        animal.setId(doc.getId());
                        listaAnimales.add(animal);
                    }
                    seleccion.notifyDataSetChanged();

                    // Si no hay animales mostramos un mensaje
                    if (listaAnimales.isEmpty()) {
                        Toast.makeText(requireContext(), "No hay animales disponibles en este centro", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al cargar animales", Toast.LENGTH_SHORT).show();
                });
    }
}