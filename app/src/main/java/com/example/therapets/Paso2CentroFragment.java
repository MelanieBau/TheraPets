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

public class Paso2CentroFragment extends Fragment {

    public Paso2CentroFragment() {
        super(R.layout.fragment_paso2_centro);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner spCentro = view.findViewById(R.id.spCentro);
        Button btn = view.findViewById(R.id.btnSiguiente2);

        String[] centros = {"Selecciona un centro", "Estrellas de Terapia", "Centro TheraPets BCN", "Centro TheraPets Madrid"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, centros);
        spCentro.setAdapter(adapter);


        if (!CitaDraftStore.centro.isEmpty()) {
            for (int i = 0; i < centros.length; i++) {
                if (centros[i].equals(CitaDraftStore.centro)) {
                    spCentro.setSelection(i);
                    break;
                }
            }
        }

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