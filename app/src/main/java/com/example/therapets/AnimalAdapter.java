package com.example.therapets;

import android.content.Intent;
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

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.ViewHolder> {

    private List<Animal> lista;
    private OnBorrarListener onBorrar;

    public interface OnBorrarListener {
        void onBorrar(Animal animal);
    }

    public AnimalAdapter(List<Animal> lista, OnEditarListener onEditar, OnBorrarListener onBorrar) {
        this.lista = lista;
        this.onBorrar = onBorrar;
    }

    // Mantenemos la interfaz para no romper el código existente
    public interface OnEditarListener {
        void onEditar(Animal animal);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_animal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Animal animal = lista.get(position);

        holder.tvNombre.setText(animal.getNombre());
        holder.tvTipo.setText("Tipo: " + animal.getTipo());
        holder.tvCentro.setText("Centro: " + animal.getCentro());

        // Cargar foto del animal con Glide
        ImageView ivFoto = holder.itemView.findViewById(R.id.ivFotoAnimalItem);
        if (animal.getFotoUrl() != null && !animal.getFotoUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(animal.getFotoUrl())
                    .centerCrop()
                    .into(ivFoto);
        } else {
            ivFoto.setBackgroundColor(holder.itemView.getContext()
                    .getColor(R.color.morado_claro));
        }


        // Abrimos EditarAnimalActivity pasando los datos del animal
        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditarAnimalActivity.class);
            intent.putExtra("animalId", animal.getId());
            intent.putExtra("nombre", animal.getNombre());
            intent.putExtra("tipo", animal.getTipo());
            intent.putExtra("raza", animal.getRaza());
            intent.putExtra("edad", animal.getEdad());
            intent.putExtra("especialidad", animal.getEspecialidad());
            intent.putExtra("fotoUrl", animal.getFotoUrl());
            v.getContext().startActivity(intent);
        });

        holder.btnBorrar.setOnClickListener(v -> onBorrar.onBorrar(animal));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvTipo, tvCentro;
        Button btnEditar, btnBorrar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreAnimal);
            tvTipo = itemView.findViewById(R.id.tvTipoAnimal);
            tvCentro = itemView.findViewById(R.id.tvCentroAnimal);
            btnEditar = itemView.findViewById(R.id.btnEditarAnimal);
            btnBorrar = itemView.findViewById(R.id.btnBorrarAnimal);
        }
    }
}