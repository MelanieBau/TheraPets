package com.example.therapets;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Paso1DatosFragment extends Fragment {

    private Toast toastActual;

    public Paso1DatosFragment() {
        super(R.layout.fragment_paso1_datos);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btnVolver).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        EditText nombres = view.findViewById(R.id.etNombres);
        EditText apellidos = view.findViewById(R.id.etApellidos);
        EditText telefono = view.findViewById(R.id.etTelefono);
        TextView fechaSeleccionada = view.findViewById(R.id.tvFechaSeleccionada);
        Button elegirFecha = view.findViewById(R.id.btnElegirFecha);
        Button siguiente = view.findViewById(R.id.btnSiguiente1);

        nombres.setText(CitaDraftStore.nombres);
        apellidos.setText(CitaDraftStore.apellidos);
        telefono.setText(CitaDraftStore.telefono);
        if (!CitaDraftStore.fecha.isEmpty()) {
            fechaSeleccionada.setText(CitaDraftStore.fecha);
            fechaSeleccionada.setTextColor(requireContext().getColor(R.color.texto_principal));
        }

        elegirFecha.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Selecciona una fecha").build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                String fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection));
                fechaSeleccionada.setText(fecha);
                fechaSeleccionada.setTextColor(requireContext().getColor(R.color.texto_principal));
                CitaDraftStore.fecha = fecha;
            });

            datePicker.show(getParentFragmentManager(), "DATE_PICKER");
        });

        siguiente.setOnClickListener(v -> {
            String nombreSrt = nombres.getText().toString().trim();

            if (nombreSrt.isEmpty()) {
                mostrarToast("Introduce tu nombre");
                return;
            }

            if (CitaDraftStore.fecha.isEmpty()) {
                mostrarToast("Selecciona una fecha");
                return;
            }

            CitaDraftStore.nombres = nombreSrt;
            CitaDraftStore.apellidos = apellidos.getText().toString().trim();
            CitaDraftStore.telefono = telefono.getText().toString().trim();

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.stepContainer, new Paso2CentroFragment()).addToBackStack(null).commit();
        });
    }

    private void mostrarToast(String mensaje) {
        if (toastActual != null) toastActual.cancel();
        toastActual = Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT);
        toastActual.show();
    }
}