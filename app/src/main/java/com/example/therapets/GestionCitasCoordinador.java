package com.example.therapets;

import android.os.Bundle;
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
import java.util.Map;

public class GestionCitasCoordinador extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<Cita> listaCitas;
    private CitaCoordinadorAdapter adapter;
    private Toast toastActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_citas_coordinador);

        db = FirebaseFirestore.getInstance();

        RecyclerView rvCitas = findViewById(R.id.rvCitasCoordinador);
        rvCitas.setLayoutManager(new LinearLayoutManager(this));

        listaCitas = new ArrayList<>();
        adapter = new CitaCoordinadorAdapter(listaCitas, cita -> confirmarCita(cita), (cita, motivo) -> cancelarCita(cita, motivo), cita -> completarCita(cita));
        rvCitas.setAdapter(adapter);

        obtenerCentroYCargarCitas();

        //Boton volver
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
    }

    private void obtenerCentroYCargarCitas() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("usuarios").document(uid).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                String centro = document.getString("centroId");
                cargarCitasDeCentro(centro);
            }
        });
    }

    private void cargarCitasDeCentro(String centro) {
        db.collection("citas").whereEqualTo("centro", centro).get().addOnSuccessListener(queryDocumentSnapshots -> {
            listaCitas.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Cita cita = doc.toObject(Cita.class);
                cita.setId(doc.getId());
                listaCitas.add(cita);
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void confirmarCita(Cita cita) {
        db.collection("citas").document(cita.getId()).update("estado", "confirmada").addOnSuccessListener(a -> obtenerCentroYCargarCitas());
    }

    private void cancelarCita(Cita cita, String motivo) {

        //Si en caso la cita es cancelada por el coordinador de centro
        Map<String, Object> datos = new HashMap<>();
        datos.put("estado", "cancelada_coordinador");
        datos.put("motivoCancelacion", motivo);

        db.collection("citas").document(cita.getId()).update(datos).addOnSuccessListener(a -> obtenerCentroYCargarCitas()).addOnFailureListener(e -> mostrarToast("Error al cancelar"));
    }

    private void completarCita(Cita cita) {
        db.collection("citas").document(cita.getId()).update("estado", "completada").addOnSuccessListener(a -> obtenerCentroYCargarCitas()).addOnFailureListener(e -> mostrarToast("Error al completar la cita"));
    }

    private void mostrarToast(String mensaje) {
        if (toastActual != null) toastActual.cancel();
        toastActual = Toast.makeText(this, mensaje, Toast.LENGTH_SHORT);
        toastActual.show();
    }
}