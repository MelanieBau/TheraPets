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
    private Toast toastActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        TextInputEditText Email = findViewById(R.id.etEmail);
        TextInputEditText Password = findViewById(R.id.etPassword);
        TextInputEditText ConfirmPassword = findViewById(R.id.etConfirmPassword);
        TextInputEditText Nombre = findViewById(R.id.etNombre);
        TextInputEditText Telefono = findViewById(R.id.etTelefono);
        TextInputEditText FechaNacimiento = findViewById(R.id.etFechaNacimiento);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        TextView tvGoLogin = findViewById(R.id.tvGoLogin);

        btnSignUp.setOnClickListener(v -> {
            String email = Email.getText().toString().trim();
            String password = Password.getText().toString().trim();
            String confirmPassword = ConfirmPassword.getText().toString().trim();
            String nombre = Nombre.getText().toString().trim();
            String telefono = Telefono.getText().toString().trim();
            String fechaNacimiento = FechaNacimiento.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || nombre.isEmpty() || telefono.isEmpty() || fechaNacimiento.isEmpty()) {
                mostrarToast("Por favor completa todos los campos");
                return;
            }
            if (!password.equals(confirmPassword)) {
                mostrarToast("Las contraseñas no coinciden");
                return;
            }
            if (password.length() < 6) {
                mostrarToast("La contraseña debe tener al menos 6 caracteres");
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String uid = mAuth.getCurrentUser().getUid();

                    //Campos para el registro
                    Map<String, Object> usuario = new HashMap<>();

                    usuario.put("nombre", nombre);
                    usuario.put("email", email);
                    usuario.put("telefono", telefono);
                    usuario.put("fechaNacimiento", fechaNacimiento);
                    usuario.put("rol", "usuario");

                    db.collection("usuarios").document(uid).set(usuario).addOnSuccessListener(a -> {
                        startActivity(new Intent(this, IniciarSesion.class));
                        finish();
                    });
                } else {
                    mostrarToast("Error al registrarse, prueba con otro email");
                }
            });
        });

        tvGoLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, IniciarSesion.class));
            finish();
        });
    }

    private void mostrarToast(String mensaje) {
        if (toastActual != null) toastActual.cancel();
        toastActual = Toast.makeText(this, mensaje, Toast.LENGTH_SHORT);
        toastActual.show();
    }
}