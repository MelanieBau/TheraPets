package com.example.therapets;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class IniciarSesion extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        // Si ya hay sesión activa, va directo al Home
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        mAuth = FirebaseAuth.getInstance();

        android.widget.EditText etCorreo = findViewById(R.id.ingresarCorreo);
        TextInputEditText etContrasena = findViewById(R.id.ingresarContrasena);
        Button btnLogin = findViewById(R.id.btnIniciarsesion);
        TextView tvOlvide = findViewById(R.id.tvOlvidarcontraseña);

        btnLogin.setOnClickListener(v -> {
            String email = etCorreo.getText().toString().trim();
            String password = etContrasena.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Bienvenida", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_LONG).show();
                        }
                    });
        });

        tvOlvide.setOnClickListener(v -> {
            Toast.makeText(this, "Próximamente: recuperar la contraseña", Toast.LENGTH_SHORT).show();
        });
    }
}