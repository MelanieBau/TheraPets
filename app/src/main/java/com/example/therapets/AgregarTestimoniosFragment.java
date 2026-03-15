package com.example.therapets;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AgregarTestimoniosFragment extends Fragment {

    public AgregarTestimoniosFragment() {
        super(R.layout.fragment_agregar_testimonio);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText etComentario = view.findViewById(R.id.etComentario);
        Button btnPublicar = view.findViewById(R.id.btnPublicarTestimonio);

        btnPublicar.setOnClickListener(v -> {
            String comentario = etComentario.getText().toString().trim();

            if (comentario.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor escribe tu experiencia", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

            // Obtener nombre del usuario desde Firestore
            FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(document -> {
                        String nombre = document.getString("nombre");

                        // Crear el testimonio
                        Map<String, Object> testimonio = new HashMap<>();
                        testimonio.put("usuarioId", uid);
                        testimonio.put("nombreUsuario", nombre);
                        testimonio.put("comentario", comentario);
                        testimonio.put("fecha", fecha);
                        testimonio.put("meGusta", 0);
                        testimonio.put("fotoUrl", "");

                        // Guardar en Firestore
                        FirebaseFirestore.getInstance()
                                .collection("testimonios")
                                .add(testimonio)
                                .addOnSuccessListener(ref -> {
                                    Toast.makeText(requireContext(), "¡Testimonio publicado! 🐾", Toast.LENGTH_SHORT).show();
                                    requireActivity().getSupportFragmentManager().popBackStack();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Error al publicar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    });
        });
    }
}