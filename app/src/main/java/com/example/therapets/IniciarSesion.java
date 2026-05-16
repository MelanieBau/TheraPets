package com.example.therapets;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

            if (!hayInternet()) {
                mostrarToast("No hay conexión a internet");
                return;
            }

            btnLogin.setEnabled(false);

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                btnLogin.setEnabled(true);

                if (task.isSuccessful()) {
                    if (mAuth.getCurrentUser() != null) {
                        verificarRolYRedirigir(mAuth.getCurrentUser().getUid());
                    }
                } else {
                    mostrarToast("Email o contraseña incorrectos");
                }
            });
        });

        tvOlvide.setOnClickListener(v ->
                startActivity(new Intent(this, RecuperarContrasena.class))
        );
    }

    private void verificarRolYRedirigir(String uid) {
        FirebaseFirestore.getInstance().collection("usuarios").document(uid).get().addOnSuccessListener(document -> {
            Intent intent;

            if (document.exists()) {
                String rol = document.getString("rol");

                if ("administrador".equals(rol)) {
                    intent = new Intent(this, Admin.class);
                } else if ("coordinador".equals(rol)) {
                    intent = new Intent(this, Coordinador.class);
                } else {
                    intent = new Intent(this, Home.class);
                }
            } else {
                intent = new Intent(this, Home.class);
            }

            // Limpiamos todo el historial para que no se pueda volver atrás al login
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }).addOnFailureListener(e -> {
            mostrarToast("Error al obtener datos del usuario");
        });
    }

    private boolean hayInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    private void mostrarToast(String mensaje) {
        if (toastActual != null) toastActual.cancel();
        toastActual = Toast.makeText(this, mensaje, Toast.LENGTH_SHORT);
        toastActual.show();
    }
}