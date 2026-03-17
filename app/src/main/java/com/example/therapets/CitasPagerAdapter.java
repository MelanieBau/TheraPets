package com.example.therapets;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CitasPagerAdapter extends FragmentStateAdapter {

    //Esta clase conecta las pestañas con su contenido.

    public CitasPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new CitasProximasFragment();
        } else {
            return new CitasPasadasFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}