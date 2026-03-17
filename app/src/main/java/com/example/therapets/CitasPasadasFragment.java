package com.example.therapets;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class CitasPasadasFragment extends Fragment {

    public CitasPasadasFragment() {
        super(R.layout.fragment_lista_citas);
    }

    //Mostrar citas anteriores del usuario:

    //Lo que hace es ir a Firestore y buscar
    //todas las citas del usuario logueado que tengan el estado "completada"
    //o sea las sesiones que ya se realizaron. Las muestra en una lista de tarjetas
    // usando el CitaAdapter con el botón de cancelar oculto, porque no tiene sentido cancelar una cita que ya pasó.

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv = view.findViewById(R.id.rvCitas);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<Cita> lista = new ArrayList<>();
        CitaAdapter adapter = new CitaAdapter(lista, false);
        rv.setAdapter(adapter);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("citas")
                .whereEqualTo("usuarioId", uid)
                .whereEqualTo("estado", "completada")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    lista.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Cita cita = doc.toObject(Cita.class);
                        cita.setId(doc.getId());
                        lista.add(cita);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}