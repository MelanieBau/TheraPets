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
import java.util.List;

public class GestionCitasCoordinadorActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<Cita> listaCitas;
    private CitaCoordinadorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_citas_coordinador);

        db = FirebaseFirestore.getInstance();

        RecyclerView rvCitas = findViewById(R.id.rvCitasCoordinador);
        rvCitas.setLayoutManager(new LinearLayoutManager(this));

        listaCitas = new ArrayList<>();
        adapter = new CitaCoordinadorAdapter(listaCitas,
                cita -> confirmarCita(cita),
                cita -> cancelarCita(cita));
        rvCitas.setAdapter(adapter);

        obtenerCentroYCargarCitas();
    }

    private void obtenerCentroYCargarCitas() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        android.util.Log.d("COORDINADOR", "UID del coordinador: " + uid);

        db.collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String centro = document.getString("centroId");
                        android.util.Log.d("COORDINADOR", "centroId encontrado: " + centro);
                        cargarCitasDeCentro(centro);
                    } else {
                        android.util.Log.d("COORDINADOR", "Documento del coordinador no existe");
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.d("COORDINADOR", "Error al obtener coordinador: " + e.getMessage());
                });
    }

    private void cargarCitasDeCentro(String centro) {
        android.util.Log.d("COORDINADOR", "Buscando citas con centro: " + centro);
        db.collection("citas")
                .whereEqualTo("centro", centro)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    android.util.Log.d("COORDINADOR", "Citas encontradas: " + queryDocumentSnapshots.size());
                    listaCitas.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Cita cita = doc.toObject(Cita.class);
                        cita.setId(doc.getId());
                        listaCitas.add(cita);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    android.util.Log.d("COORDINADOR", "Error al cargar citas: " + e.getMessage());
                    Toast.makeText(this, "Error al cargar citas", Toast.LENGTH_SHORT).show();
                });
    }

    private void confirmarCita(Cita cita) {
        db.collection("citas")
                .document(cita.getId())
                .update("estado", "confirmada")
                .addOnSuccessListener(a -> {
                    Toast.makeText(this, "Cita confirmada", Toast.LENGTH_SHORT).show();
                    obtenerCentroYCargarCitas();
                });
    }

    private void cancelarCita(Cita cita) {
        db.collection("citas")
                .document(cita.getId())
                .update("estado", "cancelada")
                .addOnSuccessListener(a -> {
                    Toast.makeText(this, "Cita cancelada", Toast.LENGTH_SHORT).show();
                    obtenerCentroYCargarCitas();
                });
    }
}