package com.example.therapets;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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
        EditText telefono = view.findViewById(R.id.etTelefono);

        TextView fechaSeleccionada = view.findViewById(R.id.tvFechaSeleccionada);
        TextView siguiente = view.findViewById(R.id.btnSiguiente1);

        //Boton para volver a la home
        view.findViewById(R.id.btnVolver).setOnClickListener(v -> requireActivity().finish());

        // Restaurar datos temporales
        nombres.setText(CitaDraftStore.nombres);
        telefono.setText(CitaDraftStore.telefono);

        // Autocompletar usuario desde Firestore
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("usuarios").document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot.exists()) {

                            String nombreUsuario = documentSnapshot.getString("nombre");
                            String telefonoUsuario = documentSnapshot.getString("telefono");

                            if (nombres.getText().toString().isEmpty() && nombreUsuario != null) {
                                nombres.setText(nombreUsuario);
                            }

                            if (telefono.getText().toString().isEmpty() && telefonoUsuario != null) {
                                telefono.setText(telefonoUsuario);
                            }
                        }

                    });

        }

        // Restaurar fecha si existe
        if (!CitaDraftStore.fecha.isEmpty()) {
            fechaSeleccionada.setText(CitaDraftStore.fecha);
            fechaSeleccionada.setTextColor(
                    requireContext().getColor(R.color.texto_principal)
            );
        }

        // Abrir calendario
        fechaSeleccionada.setOnClickListener(v -> {

            MaterialDatePicker<Long> datePicker =
                    MaterialDatePicker.Builder.datePicker().setTitleText("Selecciona una fecha").build();

            datePicker.addOnPositiveButtonClickListener(selection -> {

                String fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection));

                fechaSeleccionada.setText(fecha);
                fechaSeleccionada.setTextColor(
                        requireContext().getColor(R.color.texto_principal)
                );

                CitaDraftStore.fecha = fecha;
            });

            datePicker.show(getParentFragmentManager(), "DATE_PICKER");
        });

        // Ir a siguiente paso
        siguiente.setOnClickListener(v -> {

            String nombreStr = nombres.getText().toString().trim();

            if (nombreStr.isEmpty()) {
                mostrarToast("Introduce tu nombre");
                return;
            }

            if (CitaDraftStore.fecha.isEmpty()) {
                mostrarToast("Selecciona una fecha");
                return;
            }

            CitaDraftStore.nombres = nombreStr;
            CitaDraftStore.telefono = telefono.getText().toString().trim();

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.stepContainer, new Paso2CentroFragment()).addToBackStack(null)
                    .commit();
        });
    }

    private void mostrarToast(String mensaje) {

        if (toastActual != null) {
            toastActual.cancel();
        }

        toastActual = Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT);
        toastActual.show();
    }
}