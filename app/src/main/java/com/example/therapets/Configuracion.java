package com.example.therapets;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Configuracion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        CardView cardCambiarContrasena = findViewById(R.id.cardCambiarContrasena);
        Switch switchNotificaciones = findViewById(R.id.switchNotificaciones);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        Button btnEliminarCuenta = findViewById(R.id.btnEliminarCuenta);

        // Abre la pantalla de recuperar contraseña
        cardCambiarContrasena.setOnClickListener(v ->
                startActivity(new Intent(this, RecuperarContrasena.class)));

        // Switch de notificaciones (por ahora solo visual)
        switchNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String mensaje = isChecked ? "Notificaciones activadas" : "Notificaciones desactivadas";
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        });

        // Cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, IniciarSesion.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Eliminar cuenta con confirmación
        btnEliminarCuenta.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Eliminar cuenta")
                    .setMessage("¿Estás segura de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        // Borramos el documento del usuario en Firestore
                        FirebaseFirestore.getInstance()
                                .collection("usuarios")
                                .document(uid)
                                .delete()
                                .addOnSuccessListener(a -> {
                                    // Borramos la cuenta de Firebase Authentication
                                    FirebaseAuth.getInstance().getCurrentUser().delete()
                                            .addOnSuccessListener(b -> {
                                                Toast.makeText(this, "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(this, IniciarSesion.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            });
                                });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }
}