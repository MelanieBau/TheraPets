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

    // Interface para cuando el usuario pulsa Seleccionar
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

        // Rellenamos los datos
        holder.nombre.setText(animal.getNombre());
        holder.tipoRaza.setText(animal.getTipo() + " · " + animal.getRaza());
        holder.edad.setText(animal.getEdad());
        holder.especialidad.setText(animal.getEspecialidad());

        // Cargamos la foto con Glide
        if (animal.getFotoUrl() != null && !animal.getFotoUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(animal.getFotoUrl())
                    .centerCrop()
                    .into(holder.foto);
        }

        // Cuando pulsa Seleccionar
        holder.btnSeleccionar.setOnClickListener(v -> onSeleccionar.onSeleccionar(animal));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView foto;
        TextView nombre, tipoRaza, edad, especialidad;
        Button btnSeleccionar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.ivFotoAnimalSeleccion);
            nombre = itemView.findViewById(R.id.tvNombreAnimalSeleccion);
            tipoRaza = itemView.findViewById(R.id.tvTipoRazaSeleccion);
            edad = itemView.findViewById(R.id.tvEdadSeleccion);
            especialidad = itemView.findViewById(R.id.tvEspecialidadSeleccion);
            btnSeleccionar = itemView.findViewById(R.id.btnSeleccionarAnimal);
        }
    }
}