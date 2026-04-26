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
import java.util.Objects;

public class ValorarCitaActivity extends AppCompatActivity{

    //Variable para guardar la puntuación elegida por el usuario
    private int puntuacionSeleccionada = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valorar_cita);

        //Referencias a los elementos de la pantalla
        TextView tvCentro = findViewById(R.id.tvCentroValorar);
        TextInputEditText etNota = findViewById(R.id.etNotaValorar);
        Button btnGuardar = findViewById(R.id.btnGuardarValoracion);


        //Referencia a los emojis para los usuarios
        LinearLayout btnMal = findViewById(R.id.btnMal);
        LinearLayout btnRegular = findViewById(R.id.btnRegular);
        LinearLayout btnBien = findViewById(R.id.btnBien);
        LinearLayout btnMuyBien = findViewById(R.id.btnMuyBien);
        LinearLayout btnGenial = findViewById(R.id.btnGenial);

        //Datos de la pantalla anterior (cita)
        String citaId = getIntent().getStringExtra("citaId");
        String centro = getIntent().getStringExtra("centro");
        String fecha = getIntent().getStringExtra("fecha");

        //Mostrar el nombre del centro

        if(centro != null) {
            tvCentro.setText(centro);
        }


        //Lista de todos los emojis para resetear la selección
        LinearLayout[] emojisPaciente = {btnMal, btnRegular, btnBien, btnMuyBien, btnMal};


        //El usuario puede marcar un emoji, acorde a su sesion
        btnMal.setOnClickListener(v -> seleccionarEmoji(emojisPaciente, btnMal, 1));
        btnRegular.setOnClickListener(v -> seleccionarEmoji(emojisPaciente, btnRegular, 2));
        btnBien.setOnClickListener(v -> seleccionarEmoji(emojisPaciente, btnBien, 3));
        btnMuyBien.setOnClickListener(v -> seleccionarEmoji(emojisPaciente, btnMuyBien, 4));
        btnGenial.setOnClickListener(v -> seleccionarEmoji(emojisPaciente, btnGenial, 5));


        //El usuario puede pulsar guardar valoracion post-sesion
        btnGuardar.setOnClickListener(v -> {


            //Validación o confirmación de que el usuario seleccionó un emoji
            if (puntuacionSeleccionada == 0){
                    Toast.makeText(this, "Por favor, selecciona como te has sentido", Toast.LENGTH_SHORT).show();
                    return;
            }


            String nota = etNota.getText().toString().trim();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String fechaHoy = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());


            //Se hace la valoración con los datos
            Map<String, Object> valoracion = new HashMap<>();
            valoracion.put("usuarioId", uid);
            valoracion.put("citaId", citaId);
            valoracion.put("centro", centro);
            valoracion.put("fecha", fechaHoy);
            valoracion.put("puntuacion", puntuacionSeleccionada);
            valoracion.put("nota", nota);

            //Guardar la valoración en FireStore
            FirebaseFirestore.getInstance()
                    .collection("valoraciones")
                    .add(valoracion)
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(this, "Valoracion guardada", Toast.LENGTH_SHORT).show();
                        // Volvemos al Home después de guardar
                        Intent intent = new Intent(this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al guardar valoracion", Toast.LENGTH_SHORT).show();
                    });
        });

        }

    private void seleccionarEmoji(LinearLayout[] todos, LinearLayout seleccionado, int puntuacion){

        //Resetear los emojis
        for(LinearLayout emoji : todos) {
            emoji.setBackgroundResource(0);
        }

        //Si esta marcado tendrá el fondo morado
        seleccionado.setBackgroundResource(R.drawable.bg_emoji_seleccionado);

        //Guardamos la puntuación
        puntuacionSeleccionada = puntuacion;
    }
}
