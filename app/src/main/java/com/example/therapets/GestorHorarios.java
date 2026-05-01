package com.example.therapets;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class GestorHorarios {

    private LinearLayout contenedorDias;
    private LinearLayout contenedorHoras;
    private String[] horaSeleccionada;
    private android.content.Context context;

    public GestorHorarios(android.content.Context context,
                          LinearLayout contenedorDias,
                          LinearLayout contenedorHoras,
                          String[] horaSeleccionada) {
        this.context = context;
        this.contenedorDias = contenedorDias;
        this.contenedorHoras = contenedorHoras;
        this.horaSeleccionada = horaSeleccionada;
    }

    // Carga los días disponibles del centro
    public void cargarDias(String centroNombre) {
        FirebaseFirestore.getInstance()
                .collection("horarios")
                .whereEqualTo("centroId", centroNombre)
                .get()
                .addOnSuccessListener(snap -> {
                    List<String> dias = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snap) {
                        String dia = doc.getString("dia");
                        if (dia != null && !dias.contains(dia)) dias.add(dia);
                    }

                    if (dias.isEmpty()) {
                        Toast.makeText(context, "No hay horarios disponibles", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (String dia : dias) {
                        Button btnDia = crearBoton(dia.substring(0, 3), R.color.morado_claro, R.color.morado_principal);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                        params.setMargins(4, 0, 4, 0);
                        btnDia.setLayoutParams(params);
                        btnDia.setOnClickListener(v -> seleccionarDia(btnDia, centroNombre, dia));
                        contenedorDias.addView(btnDia);
                    }
                });
    }

    // Cuando se selecciona un día
    private void seleccionarDia(Button btnDia, String centroNombre, String dia) {
        contenedorHoras.removeAllViews();
        horaSeleccionada[0] = "";
        resaltar(contenedorDias, btnDia);
        cargarHoras(centroNombre, dia);
    }

    // Carga las horas del día seleccionado
    private void cargarHoras(String centroNombre, String dia) {
        FirebaseFirestore.getInstance()
                .collection("horarios")
                .whereEqualTo("centroId", centroNombre)
                .whereEqualTo("dia", dia)
                .get()
                .addOnSuccessListener(snap -> {
                    for (QueryDocumentSnapshot doc : snap) {
                        String hora = doc.getString("hora");
                        if (hora != null) verificarOcupado(centroNombre, hora);
                    }
                });
    }

    // Verifica si la hora está ocupada
    private void verificarOcupado(String centroNombre, String hora) {
        FirebaseFirestore.getInstance()
                .collection("citas")
                .whereEqualTo("centro", centroNombre)
                .whereEqualTo("fecha", CitaDraftStore.fecha)
                .whereEqualTo("hora", hora)
                .get()
                .addOnSuccessListener(citasSnap -> {
                    boolean ocupado = !citasSnap.isEmpty();
                    Button btnHora = crearBotonHora(hora, ocupado);
                    if (!ocupado) {
                        btnHora.setOnClickListener(v -> {
                            horaSeleccionada[0] = hora;
                            resaltar(contenedorHoras, btnHora);
                        });
                    }
                    contenedorHoras.addView(btnHora);
                });
    }

    // Crea un botón de hora con color según disponibilidad
    private Button crearBotonHora(String hora, boolean ocupado) {
        int fondo = ocupado ? R.color.coral_claro : R.color.morado_claro;
        int texto = ocupado ? R.color.coral_acento : R.color.morado_principal;
        Button btn = crearBoton(hora + (ocupado ? " — Ocupado" : " — Libre"), fondo, texto);
        btn.setEnabled(!ocupado);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 8);
        btn.setLayoutParams(params);
        return btn;
    }

    // Crea un botón con colores personalizados
    private Button crearBoton(String texto, int colorFondo, int colorTexto) {
        Button btn = new Button(context);
        btn.setText(texto);
        btn.setBackgroundTintList(ColorStateList.valueOf(context.getColor(colorFondo)));
        btn.setTextColor(context.getColor(colorTexto));
        return btn;
    }

    // Resalta el botón seleccionado y resetea los demás
    private void resaltar(LinearLayout contenedor, Button seleccionado) {
        for (int i = 0; i < contenedor.getChildCount(); i++) {
            View child = contenedor.getChildAt(i);
            if (child instanceof Button && child.isEnabled()) {
                child.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.morado_claro)));
                ((Button) child).setTextColor(context.getColor(R.color.morado_principal));
            }
        }
        seleccionado.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.morado_oscuro)));
        seleccionado.setTextColor(context.getColor(android.R.color.white));
    }
}