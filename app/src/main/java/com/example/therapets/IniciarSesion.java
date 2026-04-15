package com.example.therapets;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class IniciarSesion extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            verificarRolYRedirigir(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                            String uid = mAuth.getCurrentUser().getUid();
                            verificarRolYRedirigir(uid);
                        } else {
                            Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_LONG).show();
                        }
                    });
        });


        //Interación "he olvidado mi contraseña"

        // Interacción "he olvidado mi contraseña"
        tvOlvide.setOnClickListener(v -> {
            startActivity(new Intent(this, RecuperarContrasenaActivity.class));
        });
    }

    private void verificarRolYRedirigir(String uid) {
        FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String rol = document.getString("rol");
                        if ("administrador".equals(rol)) {
                            startActivity(new Intent(this, AdminActivity.class));
                        } else if ("coordinador".equals(rol)) {
                            startActivity(new Intent(this, CoordinadorActivity.class));
                        } else {
                            startActivity(new Intent(this, HomeActivity.class));
                        }
                        finish();
                    } else {
                        startActivity(new Intent(this, HomeActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                });
    }
}