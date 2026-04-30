
package com.example.therapets;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SeleccionarHora extends Fragment {

    private String horaSeleccionada = "";

    public SeleccionarHora() {
        super(R.layout.fragment_seleccionar_hora);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvFecha = view.findViewById(R.id.tvFechaPasoHora);
        LinearLayout contenedor = view.findViewById(R.id.contenedorHoras);
        TextView tvSinHorarios = view.findViewById(R.id.tvSinHorarios);
        Button siguiente = view.findViewById(R.id.btnSiguientePasoHora);

        // Mostramos la fecha elegida
        tvFecha.setText(CitaDraftStore.fecha);

        // Obtenemos el día de la semana de la fecha elegida
        String diaSemana = obtenerDiaSemana(CitaDraftStore.fecha);

        // Buscamos los horarios del centro elegido para ese día
        FirebaseFirestore.getInstance()
                .collection("horarios")
                .whereEqualTo("centroId", CitaDraftStore.centro)
                .whereEqualTo("dia", diaSemana)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (queryDocumentSnapshots.isEmpty()) {
                        // No hay horarios disponibles
                        tvSinHorarios.setVisibility(View.VISIBLE);
                        return;
                    }

                    // Creamos un botón por cada horario disponible
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String hora = doc.getString("hora");
                        if (hora == null) continue;

                        Button btnHora = new Button(requireContext());
                        btnHora.setText(hora);
                        btnHora.setTextColor(requireContext().getColor(R.color.texto_principal));
                        btnHora.setBackgroundTintList(
                                android.content.res.ColorStateList.valueOf(
                                        requireContext().getColor(R.color.morado_claro)));

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 0, 0, 16);
                        btnHora.setLayoutParams(params);

                        // Cuando pulsa una hora la marcamos como seleccionada
                        btnHora.setOnClickListener(v -> {
                            horaSeleccionada = hora;

                            // Reseteamos todos los botones
                            for (int i = 0; i < contenedor.getChildCount(); i++) {
                                View child = contenedor.getChildAt(i);
                                if (child instanceof Button) {
                                    child.setBackgroundTintList(
                                            android.content.res.ColorStateList.valueOf(
                                                    requireContext().getColor(R.color.morado_claro)));
                                }
                            }

                            // Marcamos el seleccionado en morado oscuro
                            btnHora.setBackgroundTintList(
                                    android.content.res.ColorStateList.valueOf(
                                            requireContext().getColor(R.color.morado_oscuro)));
                            btnHora.setTextColor(requireContext().getColor(android.R.color.white));
                        });

                        contenedor.addView(btnHora);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Error al cargar horarios", Toast.LENGTH_SHORT).show());

        // Cuando pulsa siguiente
        siguiente.setOnClickListener(v -> {
            if (horaSeleccionada.isEmpty()) {
                Toast.makeText(requireContext(), "Selecciona una hora", Toast.LENGTH_SHORT).show();
                return;
            }

            CitaDraftStore.hora = horaSeleccionada;

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.stepContainer, new Paso3CuidadorFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    // Convierte la fecha en el día de la semana en español
    private String obtenerDiaSemana(String fecha) {
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fecha);
            return new SimpleDateFormat("EEEE", new Locale("es", "ES")).format(date);
        } catch (Exception e) {
            return "";
        }
    }
}