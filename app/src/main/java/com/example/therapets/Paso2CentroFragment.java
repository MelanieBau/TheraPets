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

    private Toast toastActual;

    public Paso2CentroFragment() {
        super(R.layout.fragment_paso2_centro);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btnVolver).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        RecyclerView rv = view.findViewById(R.id.rvCentrosSeleccion);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<Centro> listaCentros = new ArrayList<>();

        ListaDeCentros adapter = new ListaDeCentros(listaCentros, (centro, hora) -> {
            CitaDraftStore.centro = centro.getNombre();
            CitaDraftStore.hora = hora;
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.stepContainer, new Paso3CuidadorFragment()).addToBackStack(null).commit();
        });

        rv.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("centros").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!isAdded()) return;
                    listaCentros.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Centro centro = doc.toObject(Centro.class);
                        centro.setId(doc.getId());
                        listaCentros.add(centro);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    mostrarToast("Error al cargar centros");
                });
    }

    private void mostrarToast(String mensaje) {
        if (toastActual != null) toastActual.cancel();
        toastActual = Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT);
        toastActual.show();
    }
}