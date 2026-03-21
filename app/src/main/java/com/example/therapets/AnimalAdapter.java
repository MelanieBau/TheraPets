package com.example.therapets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.ViewHolder> {

    private List<Animal> lista;
    private OnEditarListener onEditar;
    private OnBorrarListener onBorrar;

    public interface OnEditarListener {
        void onEditar(Animal animal);
    }

    public interface OnBorrarListener {
        void onBorrar(Animal animal);
    }

    public AnimalAdapter(List<Animal> lista, OnEditarListener onEditar, OnBorrarListener onBorrar) {
        this.lista = lista;
        this.onEditar = onEditar;
        this.onBorrar = onBorrar;
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

        holder.btnEditar.setOnClickListener(v -> onEditar.onEditar(animal));
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