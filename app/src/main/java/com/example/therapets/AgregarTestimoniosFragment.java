package com.example.therapets;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AgregarTestimoniosFragment extends Fragment {

    /** public AgregarTestimoniosFragment() {
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
    }**/

    // Variable para guardar la URI de la foto seleccionada
    private Uri fotoSeleccionada = null;
    // Código para identificar el resultado de la galería
    private static final int PICK_IMAGE = 100;

    public AgregarTestimoniosFragment() {
        super(R.layout.fragment_agregar_testimonio);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Referencias a los elementos de la pantalla
        TextInputEditText etComentario = view.findViewById(R.id.etComentario);
        Button btnPublicar = view.findViewById(R.id.btnPublicarTestimonio);
        Button btnSeleccionarFoto = view.findViewById(R.id.btnSeleccionarFoto);
        ImageView ivFotoTestimonio = view.findViewById(R.id.ivFotoTestimonio);

        // Cuando el usuario pulsa "Seleccionar foto" abre la galería
        btnSeleccionarFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        // Cuando el usuario pulsa "Publicar testimonio"
        btnPublicar.setOnClickListener(v -> {
            String comentario = etComentario.getText().toString().trim();

            if (comentario.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor escribe tu experiencia", Toast.LENGTH_SHORT).show();
                return;
            }

            // Si hay foto la subimos a Cloudinary, si no publicamos sin foto
            if (fotoSeleccionada != null) {
                subirFotoYPublicar(comentario);
            } else {
                publicarTestimonio(comentario, "");
            }
        });
    }

    // Recibimos el resultado de la galería
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            // Guardamos la URI de la foto seleccionada
            fotoSeleccionada = data.getData();
            // Mostramos la foto en el ImageView
            ImageView ivFoto = getView().findViewById(R.id.ivFotoTestimonio);
            ivFoto.setImageURI(fotoSeleccionada);
        }
    }

    // Subimos la foto a Cloudinary y luego publicamos el testimonio
    private void subirFotoYPublicar(String comentario) {
        Toast.makeText(requireContext(), "Subiendo foto...", Toast.LENGTH_SHORT).show();

        MediaManager.get().upload(fotoSeleccionada)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        // Obtenemos la URL de la foto subida
                        String fotoUrl = resultData.get("secure_url").toString();
                        // Publicamos el testimonio con la URL de la foto
                        publicarTestimonio(comentario, fotoUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(requireContext(), "Error al subir foto: " + error.getDescription(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                })
                .dispatch();
    }

    // Publicamos el testimonio en Firestore
    private void publicarTestimonio(String comentario, String fotoUrl) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        // Obtenemos el nombre del usuario desde Firestore
        FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    String nombre = document.getString("nombre");

                    // Creamos el testimonio con todos los datos
                    Map<String, Object> testimonio = new HashMap<>();
                    testimonio.put("usuarioId", uid);
                    testimonio.put("nombreUsuario", nombre);
                    testimonio.put("comentario", comentario);
                    testimonio.put("fecha", fecha);
                    testimonio.put("meGusta", 0);
                    testimonio.put("fotoUrl", fotoUrl);

                    // Guardamos en Firestore
                    FirebaseFirestore.getInstance()
                            .collection("testimonios")
                            .add(testimonio)
                            .addOnSuccessListener(ref -> {
                                Toast.makeText(requireContext(), "Testimonio publicado!", Toast.LENGTH_SHORT).show();
                                requireActivity().getSupportFragmentManager().popBackStack();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(), "Error al publicar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                });
    }
}
