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

public class Paso3CuidadorFragment extends Fragment {

    public Paso3CuidadorFragment() {
        super(R.layout.fragment_paso3_cuidador);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner sp = view.findViewById(R.id.spCuidador);
        Button btn = view.findViewById(R.id.btnSiguiente3);

        String[] cuidadores = {"Selecciona un cuidador", "Ana (Perros)", "Carlos (Gatos)", "Lucía (Conejos)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                cuidadores
        );
        sp.setAdapter(adapter);

        // Restaurar selección si vuelves atrás
        if (!CitaDraftStore.cuidador.isEmpty()) {
            for (int i = 0; i < cuidadores.length; i++) {
                if (cuidadores[i].equals(CitaDraftStore.cuidador)) {
                    sp.setSelection(i);
                    break;
                }
            }
        }

        btn.setOnClickListener(v -> {
            String selected = sp.getSelectedItem().toString();
            if (selected.equals("Selecciona un cuidador")) {
                Toast.makeText(requireContext(), "Selecciona un cuidador", Toast.LENGTH_SHORT).show();
                return;
            }

            CitaDraftStore.cuidador = selected;

            // Ir al Paso 4 (lo haremos ahora)
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.stepContainer, new Paso4MotivoFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
}