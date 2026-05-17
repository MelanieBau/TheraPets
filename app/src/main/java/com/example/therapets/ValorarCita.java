package com.example.therapets;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ValorarCita extends AppCompatActivity {

    private int puntuacionSeleccionada = 0;
    private Toast toastActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valorar_cita);

        TextView tvCentro = findViewById(R.id.tvCentroValorar);
        TextInputEditText etNota = findViewById(R.id.etNotaValorar);
        Button btnGuardar = findViewById(R.id.btnGuardarValoracion);

        LinearLayout btnMal = findViewById(R.id.btnMal);
        LinearLayout btnRegular = findViewById(R.id.btnRegular);
        LinearLayout btnBien = findViewById(R.id.btnBien);
        LinearLayout btnMuyBien = findViewById(R.id.btnMuyBien);
        LinearLayout btnGenial = findViewById(R.id.btnGenial);

        String citaId = getIntent().getStringExtra("citaId");
        String centro = getIntent().getStringExtra("centro");
        String fecha = getIntent().getStringExtra("fecha");

        if (centro != null) {
            tvCentro.setText(centro);
        }

        // Array correcto con los 5 emojis distintos
        LinearLayout[] emojisPaciente = {btnMal, btnRegular, btnBien, btnMuyBien, btnGenial};

        btnMal.setOnClickListener(v -> seleccionarEmoji(emojisPaciente, btnMal, 1));
        btnRegular.setOnClickListener(v -> seleccionarEmoji(emojisPaciente, btnRegular, 2));
        btnBien.setOnClickListener(v -> seleccionarEmoji(emojisPaciente, btnBien, 3));
        btnMuyBien.setOnClickListener(v -> seleccionarEmoji(emojisPaciente, btnMuyBien, 4));
        btnGenial.setOnClickListener(v -> seleccionarEmoji(emojisPaciente, btnGenial, 5));

        btnGuardar.setOnClickListener(v -> {
            if (puntuacionSeleccionada == 0) {
                mostrarToast("Selecciona cómo te has sentido");
                return;
            }

            String nota = etNota.getText().toString().trim();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String fechaHoy = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

            Map<String, Object> valoracion = new HashMap<>();
            valoracion.put("usuarioId", uid);
            valoracion.put("citaId", citaId);
            valoracion.put("centro", centro);
            valoracion.put("fecha", fechaHoy);
            valoracion.put("puntuacion", puntuacionSeleccionada);
            valoracion.put("nota", nota);

            FirebaseFirestore.getInstance().collection("valoraciones").add(valoracion).addOnSuccessListener(ref -> {
                Intent intent = new Intent(this, Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }).addOnFailureListener(e -> mostrarToast("Error al guardar valoración"));
        });
    }

    private void seleccionarEmoji(LinearLayout[] todos, LinearLayout seleccionado, int puntuacion) {
        for (LinearLayout emoji : todos) {
            emoji.setBackgroundResource(0);
        }
        seleccionado.setBackgroundResource(R.drawable.bg_emoji_seleccionado);
        puntuacionSeleccionada = puntuacion;
    }

    private void mostrarToast(String mensaje) {
        if (toastActual != null) toastActual.cancel();
        toastActual = Toast.makeText(this, mensaje, Toast.LENGTH_SHORT);
        toastActual.show();
    }
}