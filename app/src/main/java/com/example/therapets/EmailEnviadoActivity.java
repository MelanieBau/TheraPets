package com.example.therapets;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
public class EmailEnviadoActivity extends AppCompatActivity{

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_mail_enviado);


            //Referencias a los elementos que se muestran en la pantalla
            TextView tvEmailEnviado = findViewById(R.id.tvEmailEnviado);
            Button btnVolverAlLogin = findViewById(R.id.btnVolverAlLogin);

            //Recibir el mail que se envio
            String email = getIntent().getStringExtra("email");

            //Aquí se muestra el email en el mensaje enviado al usuario
            if (email != null) {
                tvEmailEnviado.setText("Te hemos enviado un email a " + email + "con los pasos para que puedas reestablecer tu contraseña");
            }

            //Usuario puede volver al login
            btnVolverAlLogin.setOnClickListener(v -> {
                //Volver al login cerrando todas las pantallas anteriores
                Intent intent = new Intent(this, IniciarSesion.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
        }
}
