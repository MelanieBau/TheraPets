package com.example.therapets;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Configuracion extends AppCompatActivity {

    private Toast toastActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        CardView cardCambiarContrasena = findViewById(R.id.cardCambiarContrasena);
        Switch switchNotificaciones = findViewById(R.id.switchNotificaciones);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        Button btnEliminarCuenta = findViewById(R.id.btnEliminarCuenta);

        SharedPreferences prefs = getSharedPreferences("TheraPetsPrefs", MODE_PRIVATE);
        boolean notificacionesActivas = prefs.getBoolean("notificaciones", true);
        switchNotificaciones.setChecked(notificacionesActivas);

        //Dirige a Activity recuperar/cambiar contraseña
        cardCambiarContrasena.setOnClickListener(v -> startActivity(new Intent(this, RecuperarContrasena.class)));

        switchNotificaciones.setOnClickListener(v -> {
            boolean isChecked = switchNotificaciones.isChecked();
            prefs.edit().putBoolean("notificaciones", isChecked).apply();

            String mensaje = isChecked ? "Notificaciones activadas" : "Notificaciones desactivadas";
            mostrarToast(mensaje);
        });

        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, IniciarSesion.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        //Eliminar la cuenta del usuario
        btnEliminarCuenta.setOnClickListener(v -> {
            new AlertDialog.Builder(this).setTitle("Eliminar cuenta").setMessage("¿Estás segura de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.").setPositiveButton("Eliminar", (dialog, which) -> {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                FirebaseFirestore.getInstance().collection("usuarios").document(uid).delete().addOnSuccessListener(a -> {
                    FirebaseAuth.getInstance().getCurrentUser().delete().addOnSuccessListener(b -> {
                        Intent intent = new Intent(this, IniciarSesion.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });
                });
            }).setNegativeButton("Cancelar", null).show();
        });
    }

    private void mostrarToast(String mensaje) {
        if (toastActual != null) toastActual.cancel();
        toastActual = Toast.makeText(this, mensaje, Toast.LENGTH_SHORT);
        toastActual.show();

        new android.os.Handler().postDelayed(() -> {
            if (toastActual != null) toastActual.cancel();
        }, 1000);
    }
}