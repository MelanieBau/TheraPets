package com.example.therapets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CitasFragment extends Fragment {

    public CitasFragment() {
        super(R.layout.fragment_citas);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btn = view.findViewById(R.id.btnAgendarCita);
        btn.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AgendarCitaActivity.class))
        );
    }
}