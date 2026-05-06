package com.example.therapets;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Home extends AppCompatActivity {

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavCurved bottomNav = findViewById(R.id.bottomNav);
        FloatingActionButton fab = findViewById(R.id.fabAgendar);

        // Fragment por defecto al entrar
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_inicio);
            openFragment(new InicioFragment());
        }

        // Navegación entre fragments
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                openFragment(new InicioFragment());
                return true;
            } else if (id == R.id.nav_citas) {
                openFragment(new CitasFragment());
                return true;
            } else if (id == R.id.nav_testimonios) {
                openFragment(new TestimoniosFragment());
                return true;
            } else if (id == R.id.nav_perfil) {
                openFragment(new PerfilFragment());
                return true;
            }

            return false;
        });

        // FAB central abre la pantalla de agendar cita
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AgendarCita.class)));
    }
}