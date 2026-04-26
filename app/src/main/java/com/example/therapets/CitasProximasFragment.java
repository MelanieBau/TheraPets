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

public class CitasProximasFragment extends Fragment {

    public CitasProximasFragment() {
        super(R.layout.fragment_lista_citas);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv = view.findViewById(R.id.rvCitas);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<Cita> lista = new ArrayList<>();
        CitaAdapter adapter = new CitaAdapter(lista, true);
        rv.setAdapter(adapter);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("citas")
                .whereEqualTo("usuarioId", uid)

                //Se muestra la cita en el apartado del usuario cuando esta pendiente y también cuando ya esta confirmada.
                .whereIn("estado", java.util.Arrays.asList("pendiente", "confirmada"))
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