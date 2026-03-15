package com.example.therapets;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.textfield.TextInputEditText;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        TextInputEditText etEmail = findViewById(R.id.etEmail);
        TextInputEditText etPassword = findViewById(R.id.etPassword);
        TextInputEditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        TextInputEditText etNombre = findViewById(R.id.etNombre);
        TextInputEditText etTelefono = findViewById(R.id.etTelefono);
        TextInputEditText etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        TextView tvGoLogin = findViewById(R.id.tvGoLogin);

        btnSignUp.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String nombre = etNombre.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String fechaNacimiento = etFechaNacimiento.getText().toString().trim();

            // Validaciones
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                    || nombre.isEmpty() || telefono.isEmpty() || fechaNacimiento.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear usuario en Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();

                            // Guardar datos del perfil en Firestore
                            Map<String, Object> usuario = new HashMap<>();
                            usuario.put("nombre", nombre);
                            usuario.put("email", email);
                            usuario.put("telefono", telefono);
                            usuario.put("fechaNacimiento", fechaNacimiento);
                            usuario.put("rol", "usuario");

                            db.collection("usuarios")
                                    .document(uid)
                                    .set(usuario)
                                    .addOnSuccessListener(a -> {
                                        Toast.makeText(this, "¡Cuenta creada con éxito! 🐾", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(this, IniciarSesion.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error al guardar perfil: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });

                        } else {
                            Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        tvGoLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, IniciarSesion.class));
            finish();
        });
    }
}