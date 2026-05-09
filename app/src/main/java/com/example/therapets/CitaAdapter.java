package com.example.therapets;

import android.content.Intent;
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

        // Fecha y la hora
        holder.tvFechaCita.setText(cita.getFecha() + " · " + cita.getHora());

        // Estado con colores
        holder.tvEstado.setText(cita.getEstado());
        if (cita.getEstado().equals("pendiente")) {
            holder.tvEstado.setBackgroundResource(R.drawable.bg_estado_cita);
        } else if (cita.getEstado().equals("confirmada")) {
            holder.tvEstado.setBackgroundResource(R.drawable.bg_cita_confirmada);
        }

        //Informacion en la tarjeta
        holder.tvCentroCita.setText("📍 " + cita.getCentro());
        holder.tvCuidadorCita.setText("🐾 " + cita.getCuidador());

        // Añadimos la terapeuta de la cita
        if (cita.getNombreTerapeuta() != null && !cita.getNombreTerapeuta().isEmpty()) {
            holder.tvTerapeutaCita.setVisibility(View.VISIBLE);
            holder.tvTerapeutaCita.setText("👩‍⚕️ " + cita.getNombreTerapeuta());
        } else {
            holder.tvTerapeutaCita.setVisibility(View.GONE);
        }

        holder.tvMotivoCita.setText("📋 " + cita.getMotivo());

        // Botón cancelar solo en próximas
        if (mostrarBotonCancelar) {
            holder.btnCancelarCita.setVisibility(View.VISIBLE);
            holder.btnValorarCita.setVisibility(View.GONE);

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
                        .addOnFailureListener(e ->
                                Toast.makeText(v.getContext(), "Error al cancelar", Toast.LENGTH_SHORT).show());
            });
        } else {
            holder.btnCancelarCita.setVisibility(View.GONE);

            // Botón valorar solo si está completada
            if (cita.getEstado().equals("completada")) {
                holder.btnValorarCita.setVisibility(View.VISIBLE);
                holder.btnValorarCita.setOnClickListener(v -> {
                    Intent intent = new Intent(v.getContext(), ValorarCita.class);
                    intent.putExtra("citaId", cita.getId());
                    intent.putExtra("centro", cita.getCentro());
                    intent.putExtra("fecha", cita.getFecha());
                    v.getContext().startActivity(intent);
                });
            } else {
                holder.btnValorarCita.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFechaCita, tvEstado, tvCentroCita, tvCuidadorCita, tvMotivoCita, tvTerapeutaCita;
        Button btnCancelarCita, btnValorarCita;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaCita = itemView.findViewById(R.id.tvFechaCita);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvCentroCita = itemView.findViewById(R.id.tvCentroCita);
            tvCuidadorCita = itemView.findViewById(R.id.tvCuidadorCita);
            tvMotivoCita = itemView.findViewById(R.id.tvMotivoCita);
            tvTerapeutaCita = itemView.findViewById(R.id.tvTerapeutaCita);
            btnCancelarCita = itemView.findViewById(R.id.btnCancelarCita);
            btnValorarCita = itemView.findViewById(R.id.btnValorarCita);
        }
    }
}