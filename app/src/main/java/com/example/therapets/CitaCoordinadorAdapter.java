package com.example.therapets;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CitaCoordinadorAdapter extends RecyclerView.Adapter<CitaCoordinadorAdapter.ViewHolder> {

    private List<Cita> lista;
    private OnConfirmarListener onConfirmar;
    private OnCancelarListener onCancelar;
    private OnCompletarListener onCompletar;
    private Toast toastActual;

    public interface OnConfirmarListener {
        void onConfirmar(Cita cita);
    }

    public interface OnCancelarListener {
        void onCancelar(Cita cita, String motivo);
    }

    public interface OnCompletarListener {
        void onCompletar(Cita cita);
    }

    public CitaCoordinadorAdapter(List<Cita> lista, OnConfirmarListener onConfirmar, OnCancelarListener onCancelar, OnCompletarListener onCompletar) {
        this.lista = lista;
        this.onConfirmar = onConfirmar;
        this.onCancelar = onCancelar;
        this.onCompletar = onCompletar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita_coordinador, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cita cita = lista.get(position);

        holder.tvFecha.setText(cita.getFecha() + " a las " + cita.getHora());
        holder.tvNombre.setText(cita.getNombres() + " " + cita.getApellidos());
        holder.tvCentro.setText("Centro: " + cita.getCentro());
        holder.tvCuidador.setText("Cuidador: " + cita.getCuidador());
        holder.tvMotivo.setText("Motivo: " + cita.getMotivo());
        holder.tvEstado.setText(cita.getEstado());

        if (cita.getMotivoCancelacion() != null && !cita.getMotivoCancelacion().isEmpty()) {
            holder.tvMotivoCancelacion.setVisibility(View.VISIBLE);
            holder.tvMotivoCancelacion.setText("Cancelada por usuario: " + cita.getMotivoCancelacion());
        } else {
            holder.tvMotivoCancelacion.setVisibility(View.GONE);
        }

        if (cita.getEstado().equals("pendiente")) {
            holder.btnConfirmar.setVisibility(View.VISIBLE);
            holder.btnCancelar.setVisibility(View.VISIBLE);
            holder.btnCompletar.setVisibility(View.GONE);
        } else if (cita.getEstado().equals("confirmada")) {
            holder.btnConfirmar.setVisibility(View.GONE);
            holder.btnCancelar.setVisibility(View.GONE);
            holder.btnCompletar.setVisibility(View.VISIBLE);
        } else {
            holder.btnConfirmar.setVisibility(View.GONE);
            holder.btnCancelar.setVisibility(View.GONE);
            holder.btnCompletar.setVisibility(View.GONE);
        }

        holder.btnConfirmar.setOnClickListener(v -> onConfirmar.onConfirmar(cita));
        holder.btnCompletar.setOnClickListener(v -> onCompletar.onCompletar(cita));

        holder.btnCancelar.setOnClickListener(v -> {
            EditText etMotivo = new EditText(v.getContext());
            etMotivo.setHint("Motivo de cancelación");
            etMotivo.setPadding(40, 20, 40, 20);

            new AlertDialog.Builder(v.getContext()).setTitle("Cancelar cita").setMessage("Indica el motivo para que el usuario pueda verlo").setView(etMotivo).setPositiveButton("Cancelar cita", (dialog, which) -> {
                String motivo = etMotivo.getText().toString().trim();
                if (motivo.isEmpty()) {
                    mostrarToast(v.getContext(), "Indica el motivo");
                    return;
                }
                onCancelar.onCancelar(cita, motivo);
            }).setNegativeButton("Volver", null).show();
        });
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
        TextView tvFecha, tvNombre, tvCentro, tvCuidador, tvMotivo, tvEstado, tvMotivoCancelacion;
        Button btnConfirmar, btnCancelar, btnCompletar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFechaCitaCoord);
            tvNombre = itemView.findViewById(R.id.tvNombreCitaCoord);
            tvCentro = itemView.findViewById(R.id.tvCentroCitaCoord);
            tvCuidador = itemView.findViewById(R.id.tvCuidadorCitaCoord);
            tvMotivo = itemView.findViewById(R.id.tvMotivoCitaCoord);
            tvEstado = itemView.findViewById(R.id.tvEstadoCitaCoord);
            tvMotivoCancelacion = itemView.findViewById(R.id.tvMotivoCancelacionCoord);
            btnConfirmar = itemView.findViewById(R.id.btnConfirmarCita);
            btnCancelar = itemView.findViewById(R.id.btnCancelarCitaCoord);
            btnCompletar = itemView.findViewById(R.id.btnCompletarCita);
        }
    }
}