package com.example.therapets;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AgregarTestimoniosFragment extends Fragment {

    private Uri fotoSeleccionada = null;
    private static final int PICK_IMAGE = 100;
    private float estrellasSeleccionadas = 0;

    public AgregarTestimoniosFragment() {
        super(R.layout.fragment_agregar_testimonio);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText Comentario = view.findViewById(R.id.etComentario);

        Button btnPublicar = view.findViewById(R.id.btnPublicarTestimonio);

        Button btnSeleccionarFoto = view.findViewById(R.id.btnSeleccionarFoto);

        RatingBar ratingBar = view.findViewById(R.id.ratingBar);

        //Calificar con estrellas los testimonios
        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> estrellasSeleccionadas = rating);

        btnSeleccionarFoto.setOnClickListener(v -> { Intent intent = new Intent(Intent.ACTION_PICK);intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        btnPublicar.setOnClickListener(v -> { String comentario = Comentario.getText().toString().trim();
            if (comentario.isEmpty()) {
                Toast.makeText(requireContext(), "Cuentanos tu experiencia", Toast.LENGTH_SHORT).show();
                return;
            }

            if (fotoSeleccionada != null) {
                subirFotoYPublicar(comentario);

            } else {
                publicarTestimonio(comentario, "");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            fotoSeleccionada = data.getData();
            ImageView Foto = getView().findViewById(R.id.ivFotoTestimonio);
            Foto.setImageURI(fotoSeleccionada);
        }
    }

    private void subirFotoYPublicar(String comentario) {
        Toast.makeText(requireContext(), "Subiendo la foto...", Toast.LENGTH_SHORT).show();
        MediaManager.get().upload(fotoSeleccionada).callback(new UploadCallback() {

            @Override public void onStart(String requestId) {

            }

            @Override public void onProgress(String requestId, long bytes, long totalBytes) {

            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                        String fotoUrl = resultData.get("secure_url").toString();
                        publicarTestimonio(comentario, fotoUrl);
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Error al subir foto", Toast.LENGTH_LONG).show();
            }
                    @Override public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }

    //Metodo para subir el testimonio del usuario
    private void publicarTestimonio(String comentario, String fotoUrl) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Formato de fecha
        String fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        //Accede a la coleccion de usuarios en firestore
        FirebaseFirestore.getInstance().collection("usuarios").document(uid).get().addOnSuccessListener(document -> {
                    if (!isAdded()) return;

                    //Nombre del usuario
                    String nombre = document.getString("nombre");

                    //Foto del usuario
                    String fotoUsuarioUrl = document.getString("fotoUrl") != null ? document.getString("fotoUrl") : "";

                    //Datos para subir el testimonio
                    Map<String, Object> testimonio = new HashMap<>();
                    testimonio.put("usuarioId", uid);

                    testimonio.put("nombreUsuario", nombre);

                    testimonio.put("comentario", comentario);

                    testimonio.put("fecha", fecha);

                    testimonio.put("meGusta", 0);

                    testimonio.put("fotoUrl", fotoUrl);

                    testimonio.put("fotoUsuarioUrl", fotoUsuarioUrl);

                    testimonio.put("estrellas", estrellasSeleccionadas);

                    testimonio.put("likesUsuarios", new ArrayList<>());

                    FirebaseFirestore.getInstance().collection("testimonios").add(testimonio).addOnSuccessListener(ref -> {
                                if (!isAdded()) return;
                                Toast.makeText(requireContext(), "¡Testimonio publicado!", Toast.LENGTH_SHORT).show();
                                requireActivity().getSupportFragmentManager().popBackStack();
                            })
                            .addOnFailureListener(e -> {
                                if (!isAdded()) return;
                                Toast.makeText(requireContext(), "Error al publicar", Toast.LENGTH_LONG).show();
                            });
                });
    }
}