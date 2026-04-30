package com.example.therapets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ListaHorarios extends RecyclerView.Adapter<ListaHorarios.ViewHolder> {

    private List<Horario> lista;
    private OnBorrarListener onBorrar;

    public interface OnBorrarListener {
        void onBorrar(Horario horario);
    }

    public ListaHorarios(List<Horario> lista, OnBorrarListener onBorrar) {
        this.lista = lista;
        this.onBorrar = onBorrar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_horario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Horario horario = lista.get(position);
        holder.tvDia.setText(horario.getDia());
        holder.tvHora.setText(horario.getHora());
        holder.btnBorrar.setOnClickListener(v -> onBorrar.onBorrar(horario));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDia, tvHora;
        Button btnBorrar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDia = itemView.findViewById(R.id.tvDiaHorario);
            tvHora = itemView.findViewById(R.id.tvHoraHorario);
            btnBorrar = itemView.findViewById(R.id.btnBorrarHorario);
        }
    }
}