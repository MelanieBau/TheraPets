package com.example.therapets;

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

public class AnimalSeleccion extends RecyclerView.Adapter<AnimalSeleccion.ViewHolder> {

    private List<Animal> lista;
    private OnSeleccionarListener onSeleccionar;

    public interface OnSeleccionarListener {
        void onSeleccionar(Animal animal);
    }

    public AnimalSeleccion(List<Animal> lista, OnSeleccionarListener onSeleccionar) {
        this.lista = lista;
        this.onSeleccionar = onSeleccionar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_animal_seleccion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Animal animal = lista.get(position);

        // Datos del animal
        holder.nombre.setText(animal.getNombre());
        holder.tipoRaza.setText(animal.getTipo() + " · " + animal.getRaza());
        holder.edad.setText(animal.getEdad());
        holder.especialidad.setText(animal.getEspecialidad());

        // Foto del animal
        if (animal.getFotoUrl() != null && !animal.getFotoUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(animal.getFotoUrl())
                    .centerCrop()
                    .into(holder.foto);
        }

        // Datos de la terapeuta
        holder.nombreTerapeuta.setText(animal.getNombreTerapeuta() != null
                ? animal.getNombreTerapeuta() : "Sin terapeuta asignada");
        holder.especialidadTerapeuta.setText(animal.getEspecialidadTerapeuta() != null
                ? animal.getEspecialidadTerapeuta() : "");

        // Foto de la terapeuta
        if (animal.getFotoTerapeuta() != null && !animal.getFotoTerapeuta().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(animal.getFotoTerapeuta())
                    .centerCrop()
                    .into(holder.fotoTerapeuta);
        }

        // Cuando pulsa Seleccionar
        holder.btnSeleccionar.setOnClickListener(v -> onSeleccionar.onSeleccionar(animal));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView foto, fotoTerapeuta;
        TextView nombre, tipoRaza, edad, especialidad, nombreTerapeuta, especialidadTerapeuta;
        Button btnSeleccionar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.ivFotoAnimalSeleccion);
            nombre = itemView.findViewById(R.id.tvNombreAnimalSeleccion);
            tipoRaza = itemView.findViewById(R.id.tvTipoRazaSeleccion);
            edad = itemView.findViewById(R.id.tvEdadSeleccion);
            especialidad = itemView.findViewById(R.id.tvEspecialidadSeleccion);
            fotoTerapeuta = itemView.findViewById(R.id.ivFotoTerapeutaSeleccion);
            nombreTerapeuta = itemView.findViewById(R.id.tvNombreTerapeutaSeleccion);
            especialidadTerapeuta = itemView.findViewById(R.id.tvEspecialidadTerapeutaSeleccion);
            btnSeleccionar = itemView.findViewById(R.id.btnSeleccionarAnimal);
        }
    }
}