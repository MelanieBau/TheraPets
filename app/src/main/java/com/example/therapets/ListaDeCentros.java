package com.example.therapets;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListaDeCentros extends RecyclerView.Adapter<ListaDeCentros.ViewHolder> {

    private List<Centro> lista;
    private OnConfirmarListener onConfirmar;

    public interface OnConfirmarListener {
        void onConfirmar(Centro centro, String hora);
    }

    public ListaDeCentros(List<Centro> lista, OnConfirmarListener onConfirmar) {
        this.lista = lista;
        this.onConfirmar = onConfirmar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_centro_seleccion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Centro centro = lista.get(position);
        final String[] horaSeleccionada = {""};

        holder.nombre.setText(centro.getNombre());
        holder.direccion.setText("📍 " + centro.getDireccion());
        holder.telefono.setText("📞 " + centro.getTelefono());

        if (centro.getFotoUrl() != null && !centro.getFotoUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(centro.getFotoUrl()).centerCrop().into(holder.foto);
        }

        holder.btnMaps.setOnClickListener(v -> {
            String uri = "geo:0,0?q=" + Uri.encode(centro.getDireccion());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            holder.itemView.getContext().startActivity(intent);
        });

        // Verificamos si hay horarios disponibles para el día elegido
        verificarDisponibilidad(centro.getNombre(), holder);

        // Cuando pulsa Seleccionar abrimos el dialog de horarios
        holder.btnSeleccionar.setOnClickListener(v -> {GestorHorarios gestor = new GestorHorarios(
                holder.itemView.getContext(), horaSeleccionada);

            gestor.mostrarHorasDisponibles(centro.getNombre(), hora -> {
                horaSeleccionada[0] = hora;
                holder.btnConfirmar.setText("Confirmar — " + hora);
                holder.btnConfirmar.setVisibility(View.VISIBLE);
            });
        });

        holder.btnConfirmar.setVisibility(View.GONE);

        holder.btnConfirmar.setOnClickListener(v -> {
            if (horaSeleccionada[0].isEmpty()) return;
            onConfirmar.onConfirmar(centro, horaSeleccionada[0]);
        });
    }

    private void verificarDisponibilidad(String centroNombre, ViewHolder holder) {

        // El botón se desactiva hasta que sepamos si hay horarios
        holder.btnSeleccionar.setText("Comprobando si hay citas...");
        holder.btnSeleccionar.setEnabled(false);
        holder.btnSeleccionar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(holder.itemView.getContext().getColor(android.R.color.darker_gray)));

        try {

            //Se convierte la fecha en el día de la semana (con mayúscula inicial)
            //para que coindica con el formato guardado en Firestore.
            Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(CitaDraftStore.fecha);
            String dia = new SimpleDateFormat("EEEE", new Locale("es", "ES")).format(date);
            dia = dia.substring(0, 1).toUpperCase() + dia.substring(1);
            final String diaFinal = dia;

            FirebaseFirestore.getInstance().collection("horarios").whereEqualTo("centroId", centroNombre).whereEqualTo("dia", diaFinal).get().addOnSuccessListener(snap -> {
                        if (snap.isEmpty()) {
                            holder.btnSeleccionar.setText("Sin horarios");
                            holder.btnSeleccionar.setEnabled(false);
                            holder.btnSeleccionar.setBackgroundTintList(
                                    android.content.res.ColorStateList.valueOf(holder.itemView.getContext().getColor(android.R.color.darker_gray)));
                        } else {
                            holder.btnSeleccionar.setText("Seleccionar");
                            holder.btnSeleccionar.setEnabled(true);
                            holder.btnSeleccionar.setBackgroundTintList(
                                    android.content.res.ColorStateList.valueOf(
                                            holder.itemView.getContext().getColor(R.color.morado_principal)));
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView foto;
        TextView nombre, direccion, telefono;
        Button btnMaps, btnSeleccionar, btnConfirmar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.ivFotoCentroSeleccion);
            nombre = itemView.findViewById(R.id.tvNombreCentroSeleccion);
            direccion = itemView.findViewById(R.id.tvDireccionCentroSeleccion);
            telefono = itemView.findViewById(R.id.tvTelefonoCentroSeleccion);
            btnMaps = itemView.findViewById(R.id.btnVerMaps);
            btnSeleccionar = itemView.findViewById(R.id.btnSeleccionarCentro);
            btnConfirmar = itemView.findViewById(R.id.btnConfirmarHora);
        }
    }
}