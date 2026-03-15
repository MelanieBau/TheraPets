package com.example.therapets;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InicioFragment extends Fragment {

    public InicioFragment() {
        super(R.layout.fragment_inicio);
    }

    // Frases motivacionales
    private final String[] frases = {
            "Los animales nos enseñan a vivir el presente",
            "Un abrazo peludo puede curar el alma",
            "La naturaleza y los animales son la mejor terapia",
            "Cada sesión es un paso hacia el bienestar",
            "Los animales no juzgan, solo aman",
            "Conectar con un animal es conectar contigo mismo",
            "El amor incondicional existe, lo dan los animales"
    };

    // Datos curiosos
    private final String[] datos = {
            "Acariciar un perro puede reducir el cortisol hasta un 30%, disminuyendo el estrés de forma natural.",
            "Pasar tiempo con animales reduce la presión arterial y mejora el estado de ánimo.",
            "Los gatos ronronean a una frecuencia que puede ayudar a sanar huesos y reducir el dolor.",
            "La terapia con animales se usa en hospitales, residencias y centros de salud mental.",
            "Los caballos son especialmente sensibles a las emociones humanas y reflejan nuestro estado interior.",
            "Tener una mascota reduce el riesgo de depresión y soledad en personas mayores.",
            "Los perros pueden detectar cambios en el estado emocional de las personas con gran precisión."
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvSaludo = view.findViewById(R.id.tvSaludo);
        TextView tvFecha = view.findViewById(R.id.tvFecha);
        TextView tvFrase = view.findViewById(R.id.tvFrase);
        TextView tvDatoCurioso = view.findViewById(R.id.tvDatoCurioso);
        TextView tvProximaCitaFecha = view.findViewById(R.id.tvProximaCitaFecha);
        TextView tvProximaCitaDetalle = view.findViewById(R.id.tvProximaCitaDetalle);
        Button btnAgendarCita = view.findViewById(R.id.btnAgendarCita);

        // Fecha del día
        String fecha = new SimpleDateFormat("EEEE, dd 'de' MMMM", new Locale("es", "ES")).format(new Date());
        tvFecha.setText(fecha.substring(0, 1).toUpperCase() + fecha.substring(1));

        // Frase y dato curioso del día según el día de la semana
        int diaSemana = Integer.parseInt(new SimpleDateFormat("u", Locale.getDefault()).format(new Date())) - 1;
        tvFrase.setText(frases[diaSemana % frases.length]);
        tvDatoCurioso.setText(datos[diaSemana % datos.length]);

        // Saludo con nombre del usuario
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String nombre = document.getString("nombre");
                        tvSaludo.setText("¡Hola, " + nombre + "! 🐾");
                    }
                });

        // Próxima cita del usuario
        FirebaseFirestore.getInstance()
                .collection("citas")
                .whereEqualTo("usuarioId", uid)
                .orderBy("fecha", Query.Direction.ASCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String fecha2 = queryDocumentSnapshots.getDocuments().get(0).getString("fecha");
                        String hora = queryDocumentSnapshots.getDocuments().get(0).getString("hora");
                        String centro = queryDocumentSnapshots.getDocuments().get(0).getString("centro");
                        tvProximaCitaFecha.setText("📅 " + fecha2 + " a las " + hora);
                        tvProximaCitaDetalle.setText("Centro: " + centro);
                    }
                });

        // Botón agendar cita
        btnAgendarCita.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new CitasFragment())
                    .commit();
        });
    }
}