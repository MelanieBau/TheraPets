package com.example.therapets;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class PerfilFragment extends Fragment {

    private static final int PICK_IMAGE = 100;
    private ImageView ivFotoPerfil;
    private String uid;
    private Toast toastActual;

    public PerfilFragment() {
        super(R.layout.fragment_perfil);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivFotoPerfil = view.findViewById(R.id.ivFotoPerfil);
        TextView tvNombreHeader = view.findViewById(R.id.tvNombreHeader);
        TextView tvEmailHeader = view.findViewById(R.id.tvEmailHeader);
        TextView tvTelefono = view.findViewById(R.id.tvTelefono);
        TextView tvFechaNacimiento = view.findViewById(R.id.tvFechaNacimiento);
        Button btnEditarPerfil = view.findViewById(R.id.btnEditarPerfil);
        Button btnConfiguracion = view.findViewById(R.id.btnConfiguracion);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        tvEmailHeader.setText(email);

        FirebaseFirestore.getInstance().collection("usuarios").document(uid).get().addOnSuccessListener(document -> {
            if (!isAdded()) return;
            if (document.exists()) {
                String nombre = document.getString("nombre");
                tvNombreHeader.setText(nombre);
                tvTelefono.setText(document.getString("telefono"));
                tvFechaNacimiento.setText(document.getString("fechaNacimiento"));

                String fotoUrl = document.getString("fotoUrl");
                if (fotoUrl != null && !fotoUrl.isEmpty()) {
                    Glide.with(requireContext()).load(fotoUrl).centerCrop().into(ivFotoPerfil);
                }
            }
        });

        ivFotoPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        btnEditarPerfil.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), EditarPerfil.class)));

        btnConfiguracion.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), Configuracion.class)));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri fotoSeleccionada = data.getData();
            ivFotoPerfil.setImageURI(fotoSeleccionada);
            subirFoto(fotoSeleccionada);
        }
    }

    private void subirFoto(Uri uri) {
        if (!isAdded()) return;
        MediaManager.get().upload(uri).callback(new UploadCallback() {
            @Override public void onStart(String requestId) {}
            @Override public void onProgress(String requestId, long bytes, long totalBytes) {}

            @Override
            public void onSuccess(String requestId, Map resultData) {
                if (!isAdded()) return;
                String fotoUrl = resultData.get("secure_url").toString();

                Map<String, Object> datos = new HashMap<>();
                datos.put("fotoUrl", fotoUrl);
                FirebaseFirestore.getInstance().collection("usuarios").document(uid).update(datos).addOnSuccessListener(a -> {
                    if (!isAdded()) return;
                    mostrarToast("Foto actualizada");
                });
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                if (!isAdded()) return;
                mostrarToast("Error al subir foto");
            }
            @Override public void onReschedule(String requestId, ErrorInfo error) {}
        }).dispatch();
    }

    private void mostrarToast(String mensaje) {
        if (toastActual != null) toastActual.cancel();
        toastActual = Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT);
        toastActual.show();
    }
}