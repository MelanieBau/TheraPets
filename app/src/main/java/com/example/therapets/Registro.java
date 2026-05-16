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

        //Datos para el registro
        TextInputEditText Nombre = findViewById(R.id.etNombre);
        TextInputEditText FechaNacimiento = findViewById(R.id.etFechaNacimiento);
        TextInputEditText Telefono = findViewById(R.id.etTelefono);
        TextInputEditText Email = findViewById(R.id.etEmail);
        TextInputEditText Password = findViewById(R.id.etPassword);
        TextInputEditText ConfirmPassword = findViewById(R.id.etConfirmPassword);

        //Boton de registrar
        Button btnSignUp = findViewById(R.id.btnSignUp);

        //Boton para ir al login si en caso ya tienen un user
        TextView tvGoLogin = findViewById(R.id.tvGoLogin);

        btnSignUp.setOnClickListener(v -> {

            String nombre = Nombre.getText().toString().trim();
            String fechaNacimiento = FechaNacimiento.getText().toString().trim();
            String telefono = Telefono.getText().toString().trim();
            String email = Email.getText().toString().trim();
            String password = Password.getText().toString().trim();
            String confirmPassword = ConfirmPassword.getText().toString().trim();

            //Condicionales
            if (nombre.isEmpty() || fechaNacimiento.isEmpty() || telefono.isEmpty() || email.isEmpty() || password.isEmpty() ||confirmPassword.isEmpty()) {
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

                    //Se crea un mapa (diccionario) que asocia claves de tipo String con valores de cualquier tipo (Object)
                    //Se usa HashMap como implementación para almacenar los datos
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