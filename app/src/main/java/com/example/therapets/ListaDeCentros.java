package com.example.therapets;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ListaDeCentros extends RecyclerView.Adapter<ListaDeCentros.ViewHolder> {

    private List<Centro> lista;
    private OnConfirmarListener onConfirmar;

    public interface OnConfirmarListener {
        void onConfirmar(Centro centro, String hora);
    }

    public ListaDeCentros(List<Centro> lista, OnConfirmarListener onConfirmar) {
        this.lista = lista;
        this.onConfirmar = onConfirmar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_centro_seleccion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Centro centro = lista.get(position);
        final String[] horaSeleccionada = {""};

        // Mostramos los datos del centro
        holder.nombre.setText(centro.getNombre());
        holder.direccion.setText("📍 " + centro.getDireccion());
        holder.telefono.setText("📞 " + centro.getTelefono());

        // Cargamos la foto con Glide
        if (centro.getFotoUrl() != null && !centro.getFotoUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(centro.getFotoUrl())
                    .centerCrop()
                    .into(holder.foto);
        }

        // Abrimos Google Maps con la dirección
        holder.btnMaps.setOnClickListener(v -> {
            String uri = "geo:0,0?q=" + Uri.encode(centro.getDireccion());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            holder.itemView.getContext().startActivity(intent);
        });

        // Cuando pulsa Seleccionar abrimos el dialog de horarios
        holder.btnSeleccionar.setOnClickListener(v -> {
            GestorHorarios gestor = new GestorHorarios(holder.itemView.getContext(), horaSeleccionada);

            gestor.mostrarHorasDisponibles(centro.getNombre(), hora -> {
                horaSeleccionada[0] = hora;
                // Mostramos la hora seleccionada en el botón confirmar
                holder.btnConfirmar.setText("Confirmar — " + hora);
                holder.btnConfirmar.setVisibility(View.VISIBLE);
            });
        });

        // Botón confirmar oculto hasta que se elige una hora
        holder.btnConfirmar.setVisibility(View.GONE);

        // Cuando pulsa Confirmar vamos al Paso 3
        holder.btnConfirmar.setOnClickListener(v -> {
            if (horaSeleccionada[0].isEmpty()) return;
            onConfirmar.onConfirmar(centro, horaSeleccionada[0]);
        });
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView foto;
        TextView nombre, direccion, telefono;
        Button btnMaps, btnSeleccionar, btnConfirmar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.ivFotoCentroSeleccion);
            nombre = itemView.findViewById(R.id.tvNombreCentroSeleccion);
            direccion = itemView.findViewById(R.id.tvDireccionCentroSeleccion);
            telefono = itemView.findViewById(R.id.tvTelefonoCentroSeleccion);
            btnMaps = itemView.findViewById(R.id.btnVerMaps);
            btnSeleccionar = itemView.findViewById(R.id.btnSeleccionarCentro);
            btnConfirmar = itemView.findViewById(R.id.btnConfirmarHora);
        }
    }
}