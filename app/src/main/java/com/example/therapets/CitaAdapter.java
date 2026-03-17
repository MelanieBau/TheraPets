package com.example.therapets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.ViewHolder> {

    private List<Cita> lista;
    private boolean mostrarBotonCancelar;

    public CitaAdapter(List<Cita> lista, boolean mostrarBotonCancelar) {
        this.lista = lista;
        this.mostrarBotonCancelar = mostrarBotonCancelar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cita, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cita cita = lista.get(position);

        holder.tvFechaCita.setText("Fecha " + cita.getFecha() + " a las " + cita.getHora());
        holder.tvEstado.setText(cita.getEstado());
        holder.tvCentroCita.setText("Centro" + cita.getCentro());
        holder.tvCuidadorCita.setText("Cuidador: " + cita.getCuidador());
        holder.tvMotivoCita.setText("Motivo: " + cita.getMotivo());

        // Mostrar o esconder botón cancelar
        if (mostrarBotonCancelar) {
            holder.btnCancelarCita.setVisibility(View.VISIBLE);
            holder.btnCancelarCita.setOnClickListener(v -> {
                FirebaseFirestore.getInstance()
                        .collection("citas")
                        .document(cita.getId())
                        .delete()
                        .addOnSuccessListener(a -> {
                            lista.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(v.getContext(), "Cita cancelada", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(v.getContext(), "Error al cancelar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            });
        } else {
            holder.btnCancelarCita.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFechaCita, tvEstado, tvCentroCita, tvCuidadorCita, tvMotivoCita;
        Button btnCancelarCita;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaCita = itemView.findViewById(R.id.tvFechaCita);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvCentroCita = itemView.findViewById(R.id.tvCentroCita);
            tvCuidadorCita = itemView.findViewById(R.id.tvCuidadorCita);
            tvMotivoCita = itemView.findViewById(R.id.tvMotivoCita);
            btnCancelarCita = itemView.findViewById(R.id.btnCancelarCita);
        }
    }
}