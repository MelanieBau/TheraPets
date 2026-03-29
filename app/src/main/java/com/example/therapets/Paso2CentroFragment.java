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

public class Paso2CentroFragment extends Fragment {

    public Paso2CentroFragment() {
        super(R.layout.fragment_paso2_centro);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner spCentro = view.findViewById(R.id.spCentro);
        Button btn = view.findViewById(R.id.btnSiguiente2);

        List<String> nombresCentros = new ArrayList<>();
        nombresCentros.add("Selecciona un centro");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, nombresCentros);
        spCentro.setAdapter(adapter);

        // Cargar centros desde Firestore
        FirebaseFirestore.getInstance()
                .collection("centros")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String nombre = doc.getString("nombre");
                        if (nombre != null) {
                            nombresCentros.add(nombre);
                        }
                    }
                    adapter.notifyDataSetChanged();

                    // Restaurar selección previa si existe
                    if (!CitaDraftStore.centro.isEmpty()) {
                        for (int i = 0; i < nombresCentros.size(); i++) {
                            if (nombresCentros.get(i).equals(CitaDraftStore.centro)) {
                                spCentro.setSelection(i);
                                break;
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al cargar centros", Toast.LENGTH_SHORT).show();
                });

        btn.setOnClickListener(v -> {
            String selected = spCentro.getSelectedItem().toString();
            if (selected.equals("Selecciona un centro")) {
                Toast.makeText(requireContext(), "Selecciona un centro", Toast.LENGTH_SHORT).show();
                return;
            }

            CitaDraftStore.centro = selected;

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.stepContainer, new Paso3CuidadorFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
}