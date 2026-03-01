package com.example.therapets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ConfirmacionCitaFragment extends Fragment {

    public ConfirmacionCitaFragment() {
        super(R.layout.fragment_confirmacion_cita);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvResumen = view.findViewById(R.id.tvResumen);
        Button btnVolverCitas = view.findViewById(R.id.btnVolverCitas);
        Button btnIrInicio = view.findViewById(R.id.btnIrInicio);

        String resumen =
                "Nombre: " + CitaDraftStore.nombres + " " + CitaDraftStore.apellidos + "\n" +
                        "Teléfono: " + CitaDraftStore.telefono + "\n\n" +
                        "Fecha: " + CitaDraftStore.fecha + "\n" +
                        "Hora: " + CitaDraftStore.hora + "\n\n" +
                        "Centro: " + CitaDraftStore.centro + "\n" +
                        "Cuidador: " + CitaDraftStore.cuidador + "\n\n" +
                        "Motivo: " + CitaDraftStore.motivo + "\n" +
                        (CitaDraftStore.otroMotivo.isEmpty() ? "" : ("Otros: " + CitaDraftStore.otroMotivo + "\n"));

        tvResumen.setText(resumen);

        // Vuelve a la app (tab Citas)
        btnVolverCitas.setOnClickListener(v -> requireActivity().finish());

        // Ir a Inicio: cerramos y abrimos HomeActivity seleccionando Inicio
        btnIrInicio.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("open_tab", "inicio");
            startActivity(intent);
            requireActivity().finish();
        });
    }
}