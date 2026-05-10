package com.example.therapets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;


//Mostrar animales en la home para que el usuario tenga una experiencia más agradable
public class AnimalHome extends RecyclerView.Adapter<AnimalHome.ViewHolder> {

    private List<Animal> lista;

    public AnimalHome(List<Animal> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_animal_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Animal animal = lista.get(position);
        holder.tvNombre.setText(animal.getNombre());

        if (animal.getFotoUrl() != null && !animal.getFotoUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(animal.getFotoUrl()).centerCrop().into(holder.ivFoto);
        }
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoto;
        TextView tvNombre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoto = itemView.findViewById(R.id.ivFotoAnimalHome);
            tvNombre = itemView.findViewById(R.id.tvNombreAnimalHome);
        }
    }
}