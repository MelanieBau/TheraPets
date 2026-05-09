package com.example.therapets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class InicioFragment extends Fragment {

    public InicioFragment() {
        super(R.layout.fragment_inicio);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvFecha = view.findViewById(R.id.tvFecha);
        TextView tvProximaCitaFecha = view.findViewById(R.id.tvProximaCitaFecha);
        TextView tvProximaCitaDetalle = view.findViewById(R.id.tvProximaCitaDetalle);
        CardView cardComoSurgio = view.findViewById(R.id.cardComoSurgio);

        // Emojis de estado emocional
        TextView emoji1 = view.findViewById(R.id.emoji1);
        TextView emoji2 = view.findViewById(R.id.emoji2);
        TextView emoji3 = view.findViewById(R.id.emoji3);
        TextView emoji4 = view.findViewById(R.id.emoji4);
        TextView emoji5 = view.findViewById(R.id.emoji5);

        // Fecha del día
        String fecha = new SimpleDateFormat("EEEE, dd 'de' MMMM",
                new Locale("es", "ES")).format(new Date());
        tvFecha.setText(fecha.substring(0, 1).toUpperCase() + fecha.substring(1));

        // Emojis seleccionables
        View[] emojis = {emoji1, emoji2, emoji3, emoji4, emoji5};
        for (View emoji : emojis) {
            emoji.setOnClickListener(v -> {
                // Resetear todos
                for (View e : emojis) {
                    e.setBackgroundResource(0);
                    e.setPadding(6, 6, 6, 6);
                }
                // Resaltar el seleccionado
                v.setBackgroundResource(R.drawable.bg_emoji_seleccionado);
                v.setPadding(6, 6, 6, 6);
            });
        }

        // Datos del usuario
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        // Cargar foto de perfil si existe
                        String fotoUrl = document.getString("fotoUrl");
                        if (fotoUrl != null && !fotoUrl.isEmpty()) {
                            com.bumptech.glide.Glide.with(requireContext())
                                    .load(fotoUrl)
                                    .centerCrop()
                                    .into((android.widget.ImageView) view.findViewById(R.id.ivFotoPerfil));
                        }
                    }
                });

        // Próxima cita
        FirebaseFirestore.getInstance()
                .collection("citas")
                .whereEqualTo("usuarioId", uid)
                .whereIn("estado", java.util.Arrays.asList("pendiente", "confirmada"))
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String fechaCita = queryDocumentSnapshots.getDocuments().get(0).getString("fecha");
                        String hora = queryDocumentSnapshots.getDocuments().get(0).getString("hora");
                        String terapeuta = queryDocumentSnapshots.getDocuments().get(0).getString("nombreTerapeuta");
                        tvProximaCitaFecha.setText(terapeuta != null ? terapeuta : "Sesión agendada");
                        tvProximaCitaDetalle.setText(fechaCita + " · " + hora);
                    } else {
                        tvProximaCitaFecha.setText("Sin sesiones próximas");
                        tvProximaCitaDetalle.setText("Agenda desde el botón 🐾");
                    }
                })
                .addOnFailureListener(e -> {
                    tvProximaCitaFecha.setText("Sin sesiones próximas");
                    tvProximaCitaDetalle.setText("Agenda desde el botón 🐾");
                });


        // Cargar animales en la home
        RecyclerView rvAnimales = view.findViewById(R.id.rvAnimales);
        rvAnimales.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        List<Animal> listaAnimales = new ArrayList<>();
        AnimalHome animalAdapter = new AnimalHome(listaAnimales);
        rvAnimales.setAdapter(animalAdapter);

        FirebaseFirestore.getInstance()
                .collection("animales")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!isAdded()) return;
                    listaAnimales.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Animal animal = doc.toObject(Animal.class);
                        animal.setId(doc.getId());
                        listaAnimales.add(animal);
                    }
                    animalAdapter.notifyDataSetChanged();
                });

        // Botón cómo surgió TheraPets
        cardComoSurgio.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), HistoriaTherapets.class)));
    }
}