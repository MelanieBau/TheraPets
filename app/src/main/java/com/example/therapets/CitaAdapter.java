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

        holder.tvFechaCita.setText("Fecha " + cita.getFecha() + " a las " + cita.getHora());
        holder.tvEstado.setText(cita.getEstado());

        // Cambiar color según el estado
        if (cita.getEstado().equals("pendiente")) {
            holder.tvEstado.setBackgroundResource(R.drawable.bg_estado_cita); // coral
        } else if (cita.getEstado().equals("confirmada")) {
            holder.tvEstado.setBackgroundResource(R.drawable.bg_cita_confirmada); // verde
        }

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
            //En el apartado de citas pasadas mostramos el boton de valoracion
            holder.btnCancelarCita.setVisibility(View.GONE);

            // Si la cita está completada mostramos el botón valorar
            if (cita.getEstado().equals("completada")) {
                holder.btnValorarCita.setVisibility(View.VISIBLE);
                holder.btnValorarCita.setOnClickListener(v -> {
                    // Abrimos la pantalla de valoración pasando los datos de la cita
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
        TextView tvFechaCita, tvEstado, tvCentroCita, tvCuidadorCita, tvMotivoCita;
        Button btnCancelarCita, btnValorarCita;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaCita = itemView.findViewById(R.id.tvFechaCita);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvCentroCita = itemView.findViewById(R.id.tvCentroCita);
            tvCuidadorCita = itemView.findViewById(R.id.tvCuidadorCita);
            tvMotivoCita = itemView.findViewById(R.id.tvMotivoCita);
            btnCancelarCita = itemView.findViewById(R.id.btnCancelarCita);
            btnValorarCita = itemView.findViewById(R.id.btnValorarCita);

        }
    }
}