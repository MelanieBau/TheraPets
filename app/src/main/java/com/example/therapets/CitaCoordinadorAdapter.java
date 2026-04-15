package com.example.therapets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CitaCoordinadorAdapter extends RecyclerView.Adapter<CitaCoordinadorAdapter.ViewHolder> {

    // Lista de citas y listeners para los botones
    private List<Cita> lista;
    private OnConfirmarListener onConfirmar;
    private OnCancelarListener onCancelar;

    // Interfaces para manejar los clicks de confirmar y cancelar
    public interface OnConfirmarListener {
        void onConfirmar(Cita cita);
    }

    public interface OnCancelarListener {
        void onCancelar(Cita cita);
    }

    // Constructor del adapter
    public CitaCoordinadorAdapter(List<Cita> lista, OnConfirmarListener onConfirmar, OnCancelarListener onCancelar) {
        this.lista = lista;
        this.onConfirmar = onConfirmar;
        this.onCancelar = onCancelar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el layout de cada tarjeta de cita
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cita_coordinador, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cita cita = lista.get(position);

        // Rellenamos los datos de la cita en la tarjeta
        holder.tvFecha.setText(cita.getFecha() + " a las " + cita.getHora());
        holder.tvNombre.setText(cita.getNombres() + " " + cita.getApellidos());
        holder.tvCentro.setText("Centro: " + cita.getCentro());
        holder.tvCuidador.setText("Cuidador: " + cita.getCuidador());
        holder.tvMotivo.setText("Motivo: " + cita.getMotivo());
        holder.tvEstado.setText(cita.getEstado());

        // Solo mostramos los botones si la cita está pendiente
        if (cita.getEstado().equals("pendiente")) {
            holder.btnConfirmar.setVisibility(View.VISIBLE);
            holder.btnCancelar.setVisibility(View.VISIBLE);
        } else {
            // Si ya está confirmada o cancelada ocultamos los botones
            holder.btnConfirmar.setVisibility(View.GONE);
            holder.btnCancelar.setVisibility(View.GONE);
        }

        // Cuando pulsa confirmar
        holder.btnConfirmar.setOnClickListener(v -> onConfirmar.onConfirmar(cita));

        // Cuando pulsa cancelar
        holder.btnCancelar.setOnClickListener(v -> onCancelar.onCancelar(cita));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // ViewHolder con todas las referencias a los elementos de la tarjeta
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvNombre, tvCentro, tvCuidador, tvMotivo, tvEstado;
        Button btnConfirmar, btnCancelar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFechaCitaCoord);
            tvNombre = itemView.findViewById(R.id.tvNombreCitaCoord);
            tvCentro = itemView.findViewById(R.id.tvCentroCitaCoord);
            tvCuidador = itemView.findViewById(R.id.tvCuidadorCitaCoord);
            tvMotivo = itemView.findViewById(R.id.tvMotivoCitaCoord);
            tvEstado = itemView.findViewById(R.id.tvEstadoCitaCoord);
            btnConfirmar = itemView.findViewById(R.id.btnConfirmarCita);
            btnCancelar = itemView.findViewById(R.id.btnCancelarCitaCoord);
        }
    }
}