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

public class Paso3CuidadorFragment extends Fragment {

    private Toast toastActual;

    public Paso3CuidadorFragment() {
        super(R.layout.fragment_paso3_cuidador);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btnVolver).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        RecyclerView rv = view.findViewById(R.id.rvAnimalesSeleccion);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<Animal> listaAnimales = new ArrayList<>();

        AnimalSeleccion seleccion = new AnimalSeleccion(listaAnimales, animal -> {
            CitaDraftStore.cuidador = animal.getNombre() + " (" + animal.getTipo() + ")";
            CitaDraftStore.nombreTerapeuta = animal.getNombreTerapeuta() != null ? animal.getNombreTerapeuta() : "";
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.stepContainer, new Paso4MotivoFragment()).addToBackStack(null).commit();
        });

        rv.setAdapter(seleccion);

        FirebaseFirestore.getInstance().collection("animales").whereEqualTo("centro", CitaDraftStore.centro).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!isAdded()) return;
                    listaAnimales.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Animal animal = doc.toObject(Animal.class);
                        animal.setId(doc.getId());
                        listaAnimales.add(animal);
                    }
                    seleccion.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    mostrarToast("Error al cargar animales");
                });
    }

    private void mostrarToast(String mensaje) {
        if (toastActual != null) toastActual.cancel();
        toastActual = Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT);
        toastActual.show();
    }
}