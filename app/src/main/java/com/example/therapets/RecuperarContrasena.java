package com.example.therapets;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarContrasena extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toast toastActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);

        mAuth = FirebaseAuth.getInstance();

        TextInputEditText etEmail = findViewById(R.id.etEmailRecuperar);
        Button btnEnviar = findViewById(R.id.btnEnviarRecuperar);
        Button btnVolver = findViewById(R.id.btnVolverLogin);

        btnEnviar.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (email.isEmpty()) {
                mostrarToast("Por favor introduce tu email");
                return;
            }

            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(this, EmailEnviado.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                } else {
                    mostrarToast("No se pudo enviar el email, revisa la dirección");
                }
            });
        });

        btnVolver.setOnClickListener(v -> finish());
    }

    private void mostrarToast(String mensaje) {
        if (toastActual != null) toastActual.cancel();
        toastActual = Toast.makeText(this, mensaje, Toast.LENGTH_SHORT);
        toastActual.show();
    }
}