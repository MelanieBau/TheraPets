package com.example.therapets;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class GestionCitas extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<Cita> listaCitas;
    private CitaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_citas);

        db = FirebaseFirestore.getInstance();

        RecyclerView rvCitas = findViewById(R.id.rvTodasCitas);
        rvCitas.setLayoutManager(new LinearLayoutManager(this));

        listaCitas = new ArrayList<>();
        adapter = new CitaAdapter(listaCitas, false, true);
        rvCitas.setAdapter(adapter);

        cargarTodasLasCitas();

        //Boton volver
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
    }

    private void cargarTodasLasCitas() {
        db.collection("citas").get().addOnSuccessListener(queryDocumentSnapshots -> {
            listaCitas.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Cita cita = doc.toObject(Cita.class);
                cita.setId(doc.getId());
                listaCitas.add(cita);
            }
            adapter.notifyDataSetChanged();
        });
    }
}