package com.example.therapets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class TestimonioAdapter extends RecyclerView.Adapter<TestimonioAdapter.ViewHolder> {

    private List<Testimonio> lista;
    private FragmentManager fragmentManager;

    public TestimonioAdapter(List<Testimonio> lista, FragmentManager fragmentManager) {
        this.lista = lista;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_testimonio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Testimonio t = lista.get(position);
        String uidActual = FirebaseAuth.getInstance().getCurrentUser().getUid();

        holder.tvNombreUsuario.setText(t.getNombreUsuario());
        holder.tvComentario.setText(t.getComentario());
        holder.tvFecha.setText(t.getFecha());
        holder.tvContadorMeGusta.setText(String.valueOf(t.getMeGusta()));

        // Botón me gusta
        holder.btnMeGusta.setOnClickListener(v -> {
            long nuevoContador = t.getMeGusta() + 1;
            FirebaseFirestore.getInstance()
                    .collection("testimonios")
                    .document(t.getId())
                    .update("meGusta", nuevoContador)
                    .addOnSuccessListener(a -> {
                        t.setMeGusta(nuevoContador);
                        holder.tvContadorMeGusta.setText(String.valueOf(nuevoContador));
                        // Cambiar a estrella amarilla
                        holder.btnMeGusta.setImageResource(android.R.drawable.btn_star_big_on);
                    });
        });

        // Botón borrar (solo si es el dueño del testimonio)
        if (t.getUsuarioId().equals(uidActual)) {
            holder.btnBorrar.setVisibility(View.VISIBLE);
            holder.btnBorrar.setOnClickListener(v -> {
                FirebaseFirestore.getInstance()
                        .collection("testimonios")
                        .document(t.getId())
                        .delete()
                        .addOnSuccessListener(a -> {
                            lista.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(v.getContext(), "Testimonio eliminado", Toast.LENGTH_SHORT).show();
                        });
            });
        } else {
            holder.btnBorrar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreUsuario, tvComentario, tvFecha, tvContadorMeGusta;
        ImageButton btnMeGusta, btnBorrar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreUsuario = itemView.findViewById(R.id.tvNombreUsuario);
            tvComentario = itemView.findViewById(R.id.tvComentario);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvContadorMeGusta = itemView.findViewById(R.id.tvContadorMeGusta);
            btnMeGusta = itemView.findViewById(R.id.btnMeGusta);
            btnBorrar = itemView.findViewById(R.id.btnBorrar);
        }
    }
}