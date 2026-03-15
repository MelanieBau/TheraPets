package com.example.therapets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class PerfilFragment extends Fragment {

    public PerfilFragment() {
        super(R.layout.fragment_perfil);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvNombre = view.findViewById(R.id.tvNombre);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        TextView tvTelefono = view.findViewById(R.id.tvTelefono);
        TextView tvFechaNacimiento = view.findViewById(R.id.tvFechaNacimiento);
        Button btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        Button btnEditarPerfil = view.findViewById(R.id.btnEditarPerfil);

        // Obtener el usuario logueado
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // El email lo tenemos directo de Authentication
        tvEmail.setText(email);

        // Leer el resto de datos desde Firestore
        FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        tvNombre.setText(document.getString("nombre"));
                        tvTelefono.setText(document.getString("telefono"));
                        tvFechaNacimiento.setText(document.getString("fechaNacimiento"));
                    } else {
                        Toast.makeText(requireContext(), "No se encontraron datos del perfil", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al cargar perfil: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

        // Cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(requireContext(), IniciarSesion.class));
            requireActivity().finish();
        });

        // Editar perfil (por ahora un Toast)
        btnEditarPerfil.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Próximamente: editar perfil", Toast.LENGTH_SHORT).show();
        });
    }
}