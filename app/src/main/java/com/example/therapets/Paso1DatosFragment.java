package com.example.therapets;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Paso1DatosFragment extends Fragment {

    public Paso1DatosFragment() {
        super(R.layout.fragment_paso1_datos);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText etNombres = view.findViewById(R.id.etNombres);
        EditText etApellidos = view.findViewById(R.id.etApellidos);
        EditText etTelefono = view.findViewById(R.id.etTelefono);
        EditText etFecha = view.findViewById(R.id.etFecha);
        EditText etHora = view.findViewById(R.id.etHora);
        Button btn = view.findViewById(R.id.btnSiguiente1);

        etNombres.setText(CitaDraftStore.nombres);
        etApellidos.setText(CitaDraftStore.apellidos);
        etTelefono.setText(CitaDraftStore.telefono);
        etFecha.setText(CitaDraftStore.fecha);
        etHora.setText(CitaDraftStore.hora);

        btn.setOnClickListener(v -> {
            String nombres = etNombres.getText().toString().trim();
            String fecha = etFecha.getText().toString().trim();
            String hora = etHora.getText().toString().trim();

            if (nombres.isEmpty() || fecha.isEmpty() || hora.isEmpty()) {
                Toast.makeText(requireContext(), "Completa nombres, fecha y hora", Toast.LENGTH_SHORT).show();
                return;
            }

            CitaDraftStore.nombres = nombres;
            CitaDraftStore.apellidos = etApellidos.getText().toString().trim();
            CitaDraftStore.telefono = etTelefono.getText().toString().trim();
            CitaDraftStore.fecha = fecha;
            CitaDraftStore.hora = hora;

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.stepContainer, new Paso2CentroFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
}