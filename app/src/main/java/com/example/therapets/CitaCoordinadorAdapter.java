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
    private OnCompletarListener onCompletar;

    // Interfaces para manejar los clicks de confirmar, cancelar y completar
    public interface OnConfirmarListener {
        void onConfirmar(Cita cita);
    }

    public interface OnCancelarListener {
        void onCancelar(Cita cita);
    }

    public interface OnCompletarListener {
        void onCompletar(Cita cita);
    }

    // Constructor del adapter actualizado con el listener de completar
    public CitaCoordinadorAdapter(List<Cita> lista, OnConfirmarListener onConfirmar, OnCancelarListener onCancelar, OnCompletarListener onCompletar) {
        this.lista = lista;
        this.onConfirmar = onConfirmar;
        this.onCancelar = onCancelar;
        this.onCompletar = onCompletar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

        // Lógica de botones según el estado de la cita
        if (cita.getEstado().equals("pendiente")) {
            // Cita pendiente: mostrar confirmar y cancelar
            holder.btnConfirmar.setVisibility(View.VISIBLE);
            holder.btnCancelar.setVisibility(View.VISIBLE);
            holder.btnCompletar.setVisibility(View.GONE);
        } else if (cita.getEstado().equals("confirmada")) {
            // Cita confirmada: mostrar solo completar
            holder.btnConfirmar.setVisibility(View.GONE);
            holder.btnCancelar.setVisibility(View.GONE);
            holder.btnCompletar.setVisibility(View.VISIBLE);
        } else {
            // Cita completada o cancelada: sin botones
            holder.btnConfirmar.setVisibility(View.GONE);
            holder.btnCancelar.setVisibility(View.GONE);
            holder.btnCompletar.setVisibility(View.GONE);
        }

        // Listeners de los botones
        holder.btnConfirmar.setOnClickListener(v -> onConfirmar.onConfirmar(cita));
        holder.btnCancelar.setOnClickListener(v -> onCancelar.onCancelar(cita));
        holder.btnCompletar.setOnClickListener(v -> onCompletar.onCompletar(cita));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // ViewHolder con todas las referencias incluyendo el botón completar
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvNombre, tvCentro, tvCuidador, tvMotivo, tvEstado;
        Button btnConfirmar, btnCancelar, btnCompletar;

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
            btnCompletar = itemView.findViewById(R.id.btnCompletarCita);
        }
    }
}