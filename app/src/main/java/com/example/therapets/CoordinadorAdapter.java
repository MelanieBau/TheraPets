package com.example.therapets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class CoordinadorAdapter extends RecyclerView.Adapter<CoordinadorAdapter.ViewHolder>  {

    //Lista de los coordinadores y opcion de borrar(Listener)
    private List<Usuario> lista;
    private OnBorrarListener onBorrar;

    // Interface para manejar el click de borrar
    public interface OnBorrarListener {
        void onBorrar(Usuario usuario);
    }

    //Constructor del adapter
    public CoordinadorAdapter (List<Usuario> lista, OnBorrarListener onBorrar) {
        this.lista = lista;
        this.onBorrar = onBorrar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el layout de cada tarjeta de coordinador
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_coordinador, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usuario usuario = lista.get(position);

        // Rellenamos los datos del coordinador en la tarjeta
        holder.tvNombre.setText(usuario.getNombre());
        holder.tvEmail.setText(usuario.getEmail());
        holder.tvCentro.setText("Centro: " + usuario.getCentroId());

        // Cuando el admin pulsa borrar
        holder.btnBorrar.setOnClickListener(v -> onBorrar.onBorrar(usuario));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // ViewHolder con referencias a los elementos de la tarjeta
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvEmail, tvCentro;
        Button btnBorrar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreCoord);
            tvEmail = itemView.findViewById(R.id.tvEmailCoord);
            tvCentro = itemView.findViewById(R.id.tvCentroCoord);
            btnBorrar = itemView.findViewById(R.id.btnBorrarCoord);
        }
    }

}
