package com.example.therapets;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // Fragment por defecto al entrar
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_inicio);
            openFragment(new InicioFragment());
        }

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
    }
}