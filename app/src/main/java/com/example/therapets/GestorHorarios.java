package com.example.therapets;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GestorHorarios {

    private android.content.Context context;
    private String[] horaSeleccionada;

    public GestorHorarios(android.content.Context context, String[] horaSeleccionada) {
        this.context = context;
        this.horaSeleccionada = horaSeleccionada;
    }

    private String obtenerDiaSemana() {
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(CitaDraftStore.fecha);
            String dia = new SimpleDateFormat("EEEE", new Locale("es", "ES")).format(date);
            return dia.substring(0, 1).toUpperCase() + dia.substring(1);
        } catch (Exception e) {
            return "";
        }
    }

    public void mostrarHorasDisponibles(String centroNombre, OnHoraConfirmadaListener listener) {
        String dia = obtenerDiaSemana();

        if (dia.isEmpty()) {
            Toast.makeText(context, "Error con la fecha seleccionada", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance().collection("horarios").whereEqualTo("centroId", centroNombre).whereEqualTo("dia", dia).get().addOnSuccessListener(snap -> {
            if (snap.isEmpty()) return;

            List<String> horas = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snap) {
                String hora = doc.getString("hora");
                if (hora != null) horas.add(hora);
            }

            verificarHorasYMostrarDialog(centroNombre, horas, dia, listener);
        }).addOnFailureListener(e -> Toast.makeText(context, "Error al cargar horarios", Toast.LENGTH_SHORT).show());
    }


    // Ver qué horas están libres y cuáles ocupadas, y luego muestra el diálogo de selección
    private void verificarHorasYMostrarDialog(String centroNombre, List<String> horas, String dia, OnHoraConfirmadaListener listener) {

        // Listas para separar las horas según disponibilidad
        List<String> horasLibres = new ArrayList<>();
        List<String> horasOcupadas = new ArrayList<>();

        // Contador para saber si ya termino la verificacion de disponibilidad
        final int[] pendientes = {horas.size()};

        // Verificar cada hora del horario del centro
        for (String hora : horas) {

            // Buscar si en Firebase si ya hay una cita en este centro, fecha y hora
            FirebaseFirestore.getInstance().collection("citas").whereEqualTo("centro", centroNombre).whereEqualTo("fecha", CitaDraftStore.fecha).whereEqualTo("hora", hora).get().addOnSuccessListener(citasSnap -> {
                if (citasSnap.isEmpty()) {
                    horasLibres.add(hora);
                } else {
                    horasOcupadas.add(hora);
                }
                pendientes[0]--;

                // Cuando hemos verificado todas las horas, mostramos el diálogo
                if (pendientes[0] == 0) {
                    mostrarDialog(horasLibres, horasOcupadas, dia, listener);
                }
            });
        }
    }

    private void mostrarDialog(List<String> horasLibres, List<String> horasOcupadas, String dia, OnHoraConfirmadaListener listener) {

        // Si no hay ninguna hora libre, mostramos un mensaje y no abrimos el dialog
        if (horasLibres.isEmpty()) {
            new AlertDialog.Builder(context)
                    .setTitle("Sin horarios disponibles")
                    .setMessage("Todos los horarios del " + dia + " están ocupados para este centro. Por favor, selecciona otra fecha u otro centro.")
                    .setPositiveButton("Entendido", null)
                    .show();
            return;
        }

        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        radioGroup.setPadding(40, 20, 40, 20);

        // Horas libres → seleccionables, en verde
        for (String hora : horasLibres) {
            RadioButton rb = new RadioButton(context);
            rb.setText(hora + " - Libre");
            rb.setTextColor(context.getColor(R.color.texto_principal));
            rb.setButtonTintList(ColorStateList.valueOf(context.getColor(R.color.morado_principal)));
            rb.setPadding(0, 16, 0, 16);
            rb.setTag(hora);
            radioGroup.addView(rb);
        }

        // Horas ocupadas → desactivadas, tachadas y en gris
        for (String hora : horasOcupadas) {
            RadioButton rb = new RadioButton(context);
            rb.setText(hora + " - Ocupado");
            rb.setTextColor(context.getColor(android.R.color.darker_gray));
            rb.setButtonTintList(ColorStateList.valueOf(context.getColor(android.R.color.darker_gray)));
            rb.setPaintFlags(rb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            rb.setEnabled(false);
            rb.setAlpha(0.6f);
            rb.setPadding(0, 16, 0, 16);
            radioGroup.addView(rb);
        }

        new AlertDialog.Builder(context).setTitle("Horarios disponibles - " + dia).setView(radioGroup).setPositiveButton("Confirmar", (dialog, which) -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(context, "Selecciona una hora", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton selected = radioGroup.findViewById(selectedId);
            String hora = selected.getTag().toString();
            horaSeleccionada[0] = hora;
            listener.onHoraConfirmada(hora);
        }).setNegativeButton("Cancelar", null).show();
    }

    public interface OnHoraConfirmadaListener {
        void onHoraConfirmada(String hora);
    }
}