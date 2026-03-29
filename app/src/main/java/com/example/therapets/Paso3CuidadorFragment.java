package com.example.therapets;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

        // Referencias a los elementos de la pantalla
        Spinner sp = view.findViewById(R.id.spCuidador);
        Button btn = view.findViewById(R.id.btnSiguiente3);

        // Lista que se llenará con los animales de Firestore
        List<String> nombresAnimales = new ArrayList<>();
        nombresAnimales.add("Selecciona un cuidador"); // Opción por defecto

        // Adapter que conecta la lista con el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, nombresAnimales);
        sp.setAdapter(adapter);

        // Ir a Firestore y buscar solo los animales del centro elegido en el Paso 2
        FirebaseFirestore.getInstance()
                .collection("animales")
                .whereEqualTo("centro", CitaDraftStore.centro) // Filtra por el centro del Paso 2
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Recorre cada animal encontrado y lo añade a la lista
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String nombre = doc.getString("nombre");
                        String tipo = doc.getString("tipo");
                        if (nombre != null) {
                            nombresAnimales.add(nombre + " (" + tipo + ")"); // Ej: "Max (Perro)"
                        }
                    }
                    // Actualiza el Spinner con los animales cargados
                    adapter.notifyDataSetChanged();

                    // Si el usuario volvió atrás, restaura el animal que había elegido
                    if (!CitaDraftStore.cuidador.isEmpty()) {
                        for (int i = 0; i < nombresAnimales.size(); i++) {
                            if (nombresAnimales.get(i).equals(CitaDraftStore.cuidador)) {
                                sp.setSelection(i);
                                break;
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Si falla la carga muestra un mensaje de error
                    Toast.makeText(requireContext(), "Error al cargar animales", Toast.LENGTH_SHORT).show();
                });

        btn.setOnClickListener(v -> {
            String selected = sp.getSelectedItem().toString();

            // Valida que el usuario haya seleccionado un animal
            if (selected.equals("Selecciona un cuidador")) {
                Toast.makeText(requireContext(), "Selecciona un cuidador", Toast.LENGTH_SHORT).show();
                return;
            }

            // Guarda el animal elegido y va al Paso 4
            CitaDraftStore.cuidador = selected;
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.stepContainer, new Paso4MotivoFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
}