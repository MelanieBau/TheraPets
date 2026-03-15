package com.example.therapets;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class TestimoniosFragment extends Fragment {

    public TestimoniosFragment() {
        super(R.layout.fragment_testimonios);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvTestimonios = view.findViewById(R.id.rvTestimonios);
        FloatingActionButton fabAgregar = view.findViewById(R.id.fabAgregarTestimonio);

        // Configurar RecyclerView
        rvTestimonios.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<Testimonio> lista = new ArrayList<>();
        TestimonioAdapter adapter = new TestimonioAdapter(lista, requireActivity().getSupportFragmentManager());
        rvTestimonios.setAdapter(adapter);

        // Cargar testimonios desde Firestore
        FirebaseFirestore.getInstance()
                .collection("testimonios")
                .orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    lista.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Testimonio t = doc.toObject(Testimonio.class);
                        t.setId(doc.getId());
                        lista.add(t);
                    }
                    adapter.notifyDataSetChanged();
                });

        // Botón flotante para agregar testimonio
        fabAgregar.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.stepContainer, new AgregarTestimoniosFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
}