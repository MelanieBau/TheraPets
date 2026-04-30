package com.example.therapets;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GestionHorarios extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<Horario> listaHorarios;
    private ListaHorarios adapter;
    private String centroId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_horarios);

        db = FirebaseFirestore.getInstance();

        RecyclerView rv = findViewById(R.id.rvHorarios);
        rv.setLayoutManager(new LinearLayoutManager(this));

        listaHorarios = new ArrayList<>();
        adapter = new ListaHorarios(listaHorarios, this::borrarHorario);
        rv.setAdapter(adapter);

        // Obtenemos el centroId del coordinador logueado
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(document -> {
                    centroId = document.getString("centroId");
                    cargarHorarios();
                });

        findViewById(R.id.btnAgregarHorario).setOnClickListener(v -> mostrarDialogo());
    }

    private void cargarHorarios() {
        db.collection("horarios")
                .whereEqualTo("centroId", centroId)
                .get()
                .addOnSuccessListener(snap -> {
                    listaHorarios.clear();
                    for (QueryDocumentSnapshot doc : snap) {
                        Horario horario = doc.toObject(Horario.class);
                        horario.setId(doc.getId());
                        listaHorarios.add(horario);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void mostrarDialogo() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_horario, null);

        Spinner spDia = dialogView.findViewById(R.id.spDia);
        TextView tvHora = dialogView.findViewById(R.id.tvHoraSeleccionada);
        Button btnElegirHora = dialogView.findViewById(R.id.btnElegirHora);

        // Días de la semana
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
        spDia.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dias));

        // Guardamos la hora seleccionada
        final String[] horaSeleccionada = {""};

        // Abrimos el TimePicker cuando pulsa "Elegir hora"
        btnElegirHora.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hora, minutos) -> {
                horaSeleccionada[0] = String.format(Locale.getDefault(), "%02d:%02d", hora, minutos);
                tvHora.setText(horaSeleccionada[0]);
                tvHora.setTextColor(getColor(R.color.texto_principal));
            }, 9, 0, true).show();
        });

        new AlertDialog.Builder(this)
                .setTitle("Añadir horario")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String dia = spDia.getSelectedItem().toString();

                    if (horaSeleccionada[0].isEmpty()) {
                        Toast.makeText(this, "Elige una hora", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, Object> horario = new HashMap<>();
                    horario.put("centroId", centroId);
                    horario.put("dia", dia);
                    horario.put("hora", horaSeleccionada[0]);

                    db.collection("horarios").add(horario)
                            .addOnSuccessListener(ref -> {
                                Toast.makeText(this, "Horario añadido", Toast.LENGTH_SHORT).show();
                                cargarHorarios();
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void borrarHorario(Horario horario) {
        new AlertDialog.Builder(this)
                .setTitle("Borrar horario")
                .setMessage("¿Borrar el horario del " + horario.getDia() + " a las " + horario.getHora() + "?")
                .setPositiveButton("Borrar", (dialog, which) ->
                        db.collection("horarios").document(horario.getId())
                                .delete()
                                .addOnSuccessListener(a -> {
                                    Toast.makeText(this, "Horario eliminado", Toast.LENGTH_SHORT).show();
                                    cargarHorarios();
                                }))
                .setNegativeButton("Cancelar", null)
                .show();
    }
}