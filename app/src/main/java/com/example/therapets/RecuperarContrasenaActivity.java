package com.example.therapets;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarContrasenaActivity extends AppCompatActivity {

    // Instancia de Firebase Authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);

        // Inicializamos Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Referencias a los elementos de la pantalla
        TextInputEditText etEmail = findViewById(R.id.etEmailRecuperar);
        Button btnEnviar = findViewById(R.id.btnEnviarRecuperar);
        Button btnVolver = findViewById(R.id.btnVolverLogin);

        // Cuando el usuario pulsa "Enviar email"
        btnEnviar.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            // Validamos que el email no esté vacío
            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor introduce tu email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase manda el email de recuperación
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Si se envió correctamente vamos a la pantalla de confirmación
                            Intent intent = new Intent(this, EmailEnviadoActivity.class);
                            intent.putExtra("email", email); // Pasamos el email para mostrarlo
                            startActivity(intent);
                            finish();
                        } else {
                            // Si hubo un error lo mostramos
                            Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Cuando el usuario pulsa "Volver al login"
        btnVolver.setOnClickListener(v -> {
            finish(); // Cierra esta pantalla y vuelve al login
        });
    }
}