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
    private OnSeleccionarListener onSeleccionar;

    // Interface para cuando el usuario pulsa Seleccionar
    public interface OnSeleccionarListener {
        void onSeleccionar(Centro centro);
    }

    public ListaDeCentros(List<Centro> lista, OnSeleccionarListener onSeleccionar) {
        this.lista = lista;
        this.onSeleccionar = onSeleccionar;
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

        // Rellenamos los datos
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

        // Abrir Google Maps con la dirección del centro
        holder.btnMaps.setOnClickListener(v -> {
            String uri = "geo:0,0?q=" + Uri.encode(centro.getDireccion());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            holder.itemView.getContext().startActivity(intent);
        });

        // Cuando pulsa Seleccionar
        holder.btnSeleccionar.setOnClickListener(v -> onSeleccionar.onSeleccionar(centro));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView foto;
        TextView nombre, direccion, telefono;
        Button btnMaps, btnSeleccionar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.ivFotoCentroSeleccion);
            nombre = itemView.findViewById(R.id.tvNombreCentroSeleccion);
            direccion = itemView.findViewById(R.id.tvDireccionCentroSeleccion);
            telefono = itemView.findViewById(R.id.tvTelefonoCentroSeleccion);
            btnMaps = itemView.findViewById(R.id.btnVerMaps);
            btnSeleccionar = itemView.findViewById(R.id.btnSeleccionarCentro);
        }
    }
}