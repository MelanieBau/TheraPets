package com.example.therapets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CentroAdapter extends RecyclerView.Adapter<CentroAdapter.ViewHolder> {

    private List<Centro> lista;
    private OnEditarListener onEditar;
    private OnBorrarListener onBorrar;

    public interface OnEditarListener {
        void onEditar(Centro centro);
    }

    public interface OnBorrarListener {
        void onBorrar(Centro centro);
    }

    public CentroAdapter(List<Centro> lista, OnEditarListener onEditar, OnBorrarListener onBorrar) {
        this.lista = lista;
        this.onEditar = onEditar;
        this.onBorrar = onBorrar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_centro, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Centro centro = lista.get(position);

        holder.tvNombre.setText(centro.getNombre());
        holder.tvDireccion.setText(centro.getDireccion());
        holder.tvTelefono.setText(centro.getTelefono());

        holder.btnEditar.setOnClickListener(v -> onEditar.onEditar(centro));
        holder.btnBorrar.setOnClickListener(v -> onBorrar.onBorrar(centro));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDireccion, tvTelefono;
        Button btnEditar, btnBorrar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreCentro);
            tvDireccion = itemView.findViewById(R.id.tvDireccionCentro);
            tvTelefono = itemView.findViewById(R.id.tvTelefonoCentro);
            btnEditar = itemView.findViewById(R.id.btnEditarCentro);
            btnBorrar = itemView.findViewById(R.id.btnBorrarCentro);
        }
    }
}