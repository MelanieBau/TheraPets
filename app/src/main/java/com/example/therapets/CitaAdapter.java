package com.example.therapets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.ViewHolder> {

    private List<Cita> lista;
    private boolean mostrarBotonCancelar;
    private boolean soloLectura;
    private Toast toastActual;

    public CitaAdapter(List<Cita> lista, boolean mostrarBotonCancelar) {
        this(lista, mostrarBotonCancelar, false);
    }

    public CitaAdapter(List<Cita> lista, boolean mostrarBotonCancelar, boolean soloLectura) {
        this.lista = lista;
        this.mostrarBotonCancelar = mostrarBotonCancelar;
        this.soloLectura = soloLectura;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cita cita = lista.get(position);

        holder.tvFechaCita.setText(cita.getFecha() + " · " + cita.getHora());

        // Cita con colores para identificar el estado
        String estado = cita.getEstado();

        if (estado.equals("pendiente")) {
            holder.tvEstado.setText("Pendiente");
            holder.tvEstado.setBackgroundResource(R.drawable.bg_estado_pendiente);
        } else if (estado.equals("confirmada")) {
            holder.tvEstado.setText("Confirmada");
            holder.tvEstado.setBackgroundResource(R.drawable.bg_estado_cita_confirmada);
        } else if (estado.equals("completada")) {
            holder.tvEstado.setText("Completada");
            holder.tvEstado.setBackgroundResource(R.drawable.bg_cita_completada);
        } else if (estado.equals("cancelada_usuario") || estado.equals("cancelada_coordinador")) {
            holder.tvEstado.setText("Cancelada");
            holder.tvEstado.setBackgroundResource(R.drawable.bg_estado_cancelada);
        }

        holder.tvCentroCita.setText("📍 " + cita.getCentro());
        holder.tvCuidadorCita.setText("🐾 " + cita.getCuidador());

        if (cita.getNombreTerapeuta() != null && !cita.getNombreTerapeuta().isEmpty()) {
            holder.tvTerapeutaCita.setVisibility(View.VISIBLE);
            holder.tvTerapeutaCita.setText("👩‍⚕️ " + cita.getNombreTerapeuta());
        } else {
            holder.tvTerapeutaCita.setVisibility(View.GONE);
        }

        holder.tvMotivoCita.setText("📋 " + cita.getMotivo());

        if (cita.getMotivoCancelacion() != null && !cita.getMotivoCancelacion().isEmpty()) {
            holder.tvMotivoCancelacion.setVisibility(View.VISIBLE);
            holder.tvMotivoCancelacion.setText("Cancelada: " + cita.getMotivoCancelacion());
        } else {
            holder.tvMotivoCancelacion.setVisibility(View.GONE);
        }

        if (mostrarBotonCancelar) {
            holder.btnCancelarCita.setVisibility(View.VISIBLE);
            holder.btnValorarCita.setVisibility(View.GONE);

            holder.btnCancelarCita.setOnClickListener(v -> {
                EditText etMotivo = new EditText(v.getContext());
                etMotivo.setHint("Motivo de cancelación");
                etMotivo.setPadding(40, 20, 40, 20);

                //Le aparece un aviso al usuario antes de borrar la cita
                new AlertDialog.Builder(v.getContext()).setTitle("Cancelar cita").setMessage("¿Por qué quieres cancelar esta cita?").setView(etMotivo).setPositiveButton("Cancelar cita", (dialog, which) -> {
                    String motivo = etMotivo.getText().toString().trim();

                    if (motivo.isEmpty()) {
                        mostrarToast(v.getContext(), "Indica el motivo de la cancelación");
                        return;
                    }

                    //Solo si la cita fue cancelada por el USUARIO
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("estado", "cancelada_usuario");
                    datos.put("motivoCancelacion", motivo);

                    FirebaseFirestore.getInstance().collection("citas").document(cita.getId()).update(datos).addOnSuccessListener(a -> {
                        lista.remove(position);
                        notifyItemRemoved(position);
                    }).addOnFailureListener(e ->
                            mostrarToast(v.getContext(), "Error al cancelar"));
                }).setNegativeButton("Volver", null).show();
            });
        } else {
            holder.btnCancelarCita.setVisibility(View.GONE);

            if (cita.getEstado().equals("completada") && !soloLectura) {
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

    private void mostrarToast(Context context, String mensaje) {
        if (toastActual != null) toastActual.cancel();
        toastActual = Toast.makeText(context, mensaje, Toast.LENGTH_SHORT);
        toastActual.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFechaCita, tvEstado, tvCentroCita, tvCuidadorCita, tvMotivoCita, tvTerapeutaCita, tvMotivoCancelacion;
        Button btnCancelarCita, btnValorarCita;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaCita = itemView.findViewById(R.id.tvFechaCita);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvCentroCita = itemView.findViewById(R.id.tvCentroCita);
            tvCuidadorCita = itemView.findViewById(R.id.tvCuidadorCita);
            tvMotivoCita = itemView.findViewById(R.id.tvMotivoCita);
            tvTerapeutaCita = itemView.findViewById(R.id.tvTerapeutaCita);
            tvMotivoCancelacion = itemView.findViewById(R.id.tvMotivoCancelacion);
            btnCancelarCita = itemView.findViewById(R.id.btnCancelarCita);
            btnValorarCita = itemView.findViewById(R.id.btnValorarCita);
        }
    }
}