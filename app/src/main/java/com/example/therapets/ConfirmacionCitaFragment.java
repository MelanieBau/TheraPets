package com.example.therapets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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

        btnVolverCitas.setOnClickListener(v -> requireActivity().finish());

        btnIrInicio.setOnClickListener(v -> guardarCitaEIrInicio());
    }

    private void guardarCitaEIrInicio() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Cita cita = new Cita();
        cita.setNombres(CitaDraftStore.nombres);
        cita.setApellidos(CitaDraftStore.apellidos);
        cita.setTelefono(CitaDraftStore.telefono);
        cita.setFecha(CitaDraftStore.fecha);
        cita.setHora(CitaDraftStore.hora);
        cita.setCentro(CitaDraftStore.centro);
        cita.setCuidador(CitaDraftStore.cuidador);
        cita.setMotivo(
                CitaDraftStore.otroMotivo.isEmpty()
                        ? CitaDraftStore.motivo
                        : CitaDraftStore.motivo + " - " + CitaDraftStore.otroMotivo
        );
        cita.setEstado("pendiente");
        cita.setUsuarioId(uid);

        FirebaseFirestore.getInstance()
                .collection("citas")
                .add(cita)
                .addOnSuccessListener(documentReference -> {
                    CitaDraftStore.clear();

                    Intent intent = new Intent(requireContext(), Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("open_tab", "inicio");
                    startActivity(intent);
                    requireActivity().finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al guardar la cita", Toast.LENGTH_SHORT).show();
                });
    }
}