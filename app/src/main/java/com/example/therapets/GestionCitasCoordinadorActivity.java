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


public class GestionCitasCoordinadorActivity extends AppCompatActivity{

    //Variables principales

    private FirebaseFirestore db;
    private List<Cita> listaCitas;
    private CitaCoordinadorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_citas_coordinador);

        //Inciar Firestore

        db = FirebaseFirestore.getInstance();

        //Configuración del RecyclerView
        RecyclerView rvCitas = findViewById(R.id.rvCitasCoordinador);
        rvCitas.setLayoutManager(new LinearLayoutManager(this));

        //Inicio de la lista de citas y el Adapter

        listaCitas = new ArrayList<>();
        adapter = new CitaCoordinadorAdapter(listaCitas,
                // Cuando el coordinador pulsa Confirmar
                cita -> confirmarCita(cita),
                // Cuando el coordinador pulsa Cancelar
                cita -> cancelarCita(cita));
        rvCitas.setAdapter(adapter);

        //Centro del coordinador y carga de sus citas
        obtenerCentroYCargasCitas();
    }

    private void obtenerCentroYCargarCitas() {
        // Obtenemos el UID del coordinador logueado
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Leemos el centroId del coordinador desde Firestore
        db.collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String centro = document.getString("centroId");
                        // Cargamos solo las citas de su centro
                        cargarCitasDeCentro(centro);
                    }
                });
    }

    private void cargarCitasDeCentro(String centro) {
        // Buscamos en Firestore las citas que pertenecen a este centro
        db.collection("citas")
                .whereEqualTo("centro", centro)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaCitas.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Cita cita = doc.toObject(Cita.class);
                        cita.setId(doc.getId());
                        listaCitas.add(cita);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar citas", Toast.LENGTH_SHORT).show();
                });
    }

    private void confirmarCita(Cita cita) {
        // Actualizamos el estado de la cita a "confirmada" en Firestore
        db.collection("citas")
                .document(cita.getId())
                .update("estado", "confirmada")
                .addOnSuccessListener(a -> {
                    Toast.makeText(this, "Cita confirmada", Toast.LENGTH_SHORT).show();
                    // Recargamos la lista para reflejar el cambio
                    obtenerCentroYCargarCitas();
                });
    }

    private void cancelarCita(Cita cita) {
        // Actualizamos el estado de la cita a "cancelada" en Firestore
        db.collection("citas")
                .document(cita.getId())
                .update("estado", "cancelada")
                .addOnSuccessListener(a -> {
                    Toast.makeText(this, "Cita cancelada", Toast.LENGTH_SHORT).show();
                    // Recargamos la lista para reflejar el cambio
                    obtenerCentroYCargarCitas();
                });
    }
}