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
    private Toast toastActual;

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
                mostrarToast("Por favor completa todos los campos");
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    verificarRolYRedirigir(mAuth.getCurrentUser().getUid());
                } else {
                    mostrarToast("Email o contraseña incorrectos");
                }
            });
        });

        tvOlvide.setOnClickListener(v -> startActivity(new Intent(this, RecuperarContrasena.class)));
    }

    private void verificarRolYRedirigir(String uid) {
        FirebaseFirestore.getInstance().collection("usuarios").document(uid).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                String rol = document.getString("rol");
                if ("administrador".equals(rol)) {
                    startActivity(new Intent(this, Admin.class));
                } else if ("coordinador".equals(rol)) {
                    startActivity(new Intent(this, Coordinador.class));
                } else {
                    startActivity(new Intent(this, Home.class));
                }
                finish();

            } else {

                startActivity(new Intent(this, Home.class));
                finish();
            }
        }).addOnFailureListener(e -> {
            startActivity(new Intent(this, Home.class));
            finish();
        });
    }

    private void mostrarToast(String mensaje) {
        if (toastActual != null) toastActual.cancel();
        toastActual = Toast.makeText(this, mensaje, Toast.LENGTH_SHORT);
        toastActual.show();
    }
}