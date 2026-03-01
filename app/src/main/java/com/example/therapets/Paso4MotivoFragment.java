package com.example.therapets;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Paso4MotivoFragment extends Fragment {

    public Paso4MotivoFragment() {
        super(R.layout.fragment_paso4_motivo);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner spMotivo = view.findViewById(R.id.spMotivo);
        EditText etOtro = view.findViewById(R.id.etOtroMotivo);
        Button btnFinalizar = view.findViewById(R.id.btnFinalizar);

        String[] motivos = {
                "Selecciona un motivo",
                "Ansiedad",
                "Estrés",
                "Depresión",
                "Acompañamiento emocional",
                "Otro"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                motivos
        );
        spMotivo.setAdapter(adapter);

        // Restaurar si vuelves atrás
        if (!CitaDraftStore.motivo.isEmpty()) {
            for (int i = 0; i < motivos.length; i++) {
                if (motivos[i].equals(CitaDraftStore.motivo)) {
                    spMotivo.setSelection(i);
                    break;
                }
            }
        }
        etOtro.setText(CitaDraftStore.otroMotivo);

        btnFinalizar.setOnClickListener(v -> {
            String selected = spMotivo.getSelectedItem().toString();
            if (selected.equals("Selecciona un motivo")) {
                Toast.makeText(requireContext(), "Selecciona un motivo", Toast.LENGTH_SHORT).show();
                return;
            }

            CitaDraftStore.motivo = selected;
            CitaDraftStore.otroMotivo = etOtro.getText().toString().trim();

            // Ir a confirmación con resumen
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.stepContainer, new ConfirmacionCitaFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
}
