package com.example.therapets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import androidx.viewpager2.widget.ViewPager2;

public class CitasFragment extends Fragment {

    public CitasFragment() {
        super(R.layout.fragment_citas);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurar pestañas
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        CitasPagerAdapter adapter = new CitasPagerAdapter(
                getChildFragmentManager(),
                getLifecycle()
        );
        viewPager.setAdapter(adapter);

        // Conectar pestañas con ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Próximas");
            } else {
                tab.setText("Pasadas");
            }
        }).attach();

        // Botón agendar nueva cita
        Button btnNuevaCita = view.findViewById(R.id.btnNuevaCita);
        btnNuevaCita.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AgendarCitaActivity.class))
        );
    }
}