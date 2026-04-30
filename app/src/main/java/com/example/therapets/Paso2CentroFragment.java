package com.example.therapets;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class Paso2CentroFragment extends Fragment {

    public Paso2CentroFragment() {
        super(R.layout.fragment_paso2_centro);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv = view.findViewById(R.id.rvCentrosSeleccion);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<Centro> listaCentros = new ArrayList<>();

        // Cuando el usuario selecciona un centro va al paso de horas
        ListaDeCentros adapter = new ListaDeCentros(listaCentros, centro -> {
            CitaDraftStore.centro = centro.getNombre();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.stepContainer, new SeleccionarHora())
                    .addToBackStack(null)
                    .commit();
        });

        rv.setAdapter(adapter);

        // Cargamos los centros desde Firestore
        FirebaseFirestore.getInstance()
                .collection("centros")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaCentros.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Centro centro = doc.toObject(Centro.class);
                        centro.setId(doc.getId());
                        listaCentros.add(centro);
                    }
                    adapter.notifyDataSetChanged();

                    if (listaCentros.isEmpty()) {
                        Toast.makeText(requireContext(), "No hay centros disponibles", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Error al cargar centros", Toast.LENGTH_SHORT).show());
    }
}