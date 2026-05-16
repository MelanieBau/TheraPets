package com.example.therapets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InicioFragment extends Fragment {

    public InicioFragment() {
        super(R.layout.fragment_inicio);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvFecha = view.findViewById(R.id.tvFecha);
        TextView tvSaludo = view.findViewById(R.id.tvSaludo);
        TextView tvProximaCitaFecha = view.findViewById(R.id.tvProximaCitaFecha);
        CardView cardComoSurgio = view.findViewById(R.id.cardComoSurgio);

        ImageView ivFotoPerfil = view.findViewById(R.id.ivFotoPerfil);
        ImageView ivFotoTerapeutaCita = view.findViewById(R.id.ivFotoTerapeutaCita);

        tvSaludo.setText("¡Hola!");

        // Fecha
        String fecha = new SimpleDateFormat("EEEE, dd 'de' MMMM",
                new Locale("es", "ES")).format(new Date());

        tvFecha.setText(fecha.substring(0, 1).toUpperCase() + fecha.substring(1));

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (uid == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Usuario therapets
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(document -> {

                    if (!isAdded() || !document.exists()) return;

                    String nombre = document.getString("nombre");

                    if (nombre != null && !nombre.isEmpty()) {
                        String primerNombre = nombre.split(" ")[0];
                        primerNombre = primerNombre.substring(0, 1).toUpperCase() + primerNombre.substring(1).toLowerCase();

                        tvSaludo.setText("¡Hola, " + primerNombre + "!");
                    }

                    String fotoUrl = document.getString("fotoUrl");

                    if (fotoUrl != null && !fotoUrl.isEmpty()) {
                        Glide.with(requireContext()).load(fotoUrl).centerCrop().into(ivFotoPerfil);
                    }
                });

        // Próxima cita
        db.collection("citas").whereEqualTo("usuarioId", uid).whereIn("estado", Arrays.asList("pendiente", "confirmada")).limit(1).get().addOnSuccessListener(snapshot -> {

                    if (!isAdded()) return;

                    if (!snapshot.isEmpty()) {

                        QueryDocumentSnapshot doc =
                                (QueryDocumentSnapshot) snapshot.getDocuments().get(0);

                        String fechaCita = doc.getString("fecha");
                        String hora = doc.getString("hora");
                        String terapeuta = doc.getString("nombreTerapeuta");
                        String cuidador = doc.getString("cuidador");

                        tvProximaCitaFecha.setText(
                                terapeuta != null ? terapeuta : "Sesión agendada"
                        );


                        if (cuidador != null && cuidador.contains("(")) {

                            String nombreAnimal = cuidador.split(" \\(")[0];

                            db.collection("animales").whereEqualTo("nombre", nombreAnimal).limit(1).get().addOnSuccessListener(snap -> {

                                        if (!isAdded() || snap.isEmpty()) return;

                                        String fotoTerapeuta =
                                                snap.getDocuments().get(0).getString("fotoTerapeuta");

                                        if (fotoTerapeuta != null && !fotoTerapeuta.isEmpty()) {
                                            Glide.with(requireContext()).load(fotoTerapeuta).centerCrop().into(ivFotoTerapeutaCita);
                                        }
                                    });
                        }

                    } else {
                        tvProximaCitaFecha.setText("Sin sesiones próximas");
                    }
                });

        // Animales
        RecyclerView rvAnimales = view.findViewById(R.id.rvAnimales);
        rvAnimales.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        List<Animal> listaAnimales = new ArrayList<>();
        List<Animal> listaAnimalesCompleta = new ArrayList<>();
        AnimalHome adapter = new AnimalHome(listaAnimales);
        rvAnimales.setAdapter(adapter);

        db.collection("animales").get().addOnSuccessListener(snapshot -> {

                    if (!isAdded()) return;

                    listaAnimalesCompleta.clear();

                    for (QueryDocumentSnapshot doc : snapshot) {
                        Animal animal = doc.toObject(Animal.class);
                        animal.setId(doc.getId());
                        listaAnimalesCompleta.add(animal);
                    }

                    listaAnimales.clear();
                    listaAnimales.addAll(listaAnimalesCompleta);
                    adapter.notifyDataSetChanged();
                });

        // Chip de filtro para los animales de la app
        ChipGroup chipGroup = view.findViewById(R.id.chipGroupFiltros);
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            listaAnimales.clear();

            if (checkedId == R.id.chipTodos) {
                listaAnimales.addAll(listaAnimalesCompleta);
            } else if (checkedId == R.id.chipPerros) {
                for (Animal a : listaAnimalesCompleta) {
                    if (a.getTipo() != null && a.getTipo().toLowerCase().contains("perro")) listaAnimales.add(a);
                }
            } else if (checkedId == R.id.chipGatos) {
                for (Animal a : listaAnimalesCompleta) {
                    if (a.getTipo() != null && a.getTipo().toLowerCase().contains("gato")) listaAnimales.add(a);
                }
            } else if (checkedId == R.id.chipConejos) {
                for (Animal a : listaAnimalesCompleta) {
                    if (a.getTipo() != null && a.getTipo().toLowerCase().contains("conejo")) listaAnimales.add(a);
                }
            } else if (checkedId == R.id.chipCaballos) {
                for (Animal a : listaAnimalesCompleta) {
                    if (a.getTipo() != null && a.getTipo().toLowerCase().contains("caballo")) listaAnimales.add(a);
                }
            } else if (checkedId == R.id.chipCobayas) {
                for (Animal a : listaAnimalesCompleta) {
                    if (a.getTipo() != null && a.getTipo().toLowerCase().contains("cobaya")) listaAnimales.add(a);
                }
            }

            adapter.notifyDataSetChanged();
        });


        // Historia therapets
        cardComoSurgio.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), HistoriaTherapets.class))
        );
    }
}