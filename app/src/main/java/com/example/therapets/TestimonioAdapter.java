package com.example.therapets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
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

        // Estrellas
        if (holder.ratingBar != null) {
            holder.ratingBar.setRating(t.getEstrellas());
        }

        // Foto del testimonio
        if (t.getFotoUrl() != null && !t.getFotoUrl().isEmpty()) {
            holder.ivFotoAnimal.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(t.getFotoUrl())
                    .centerCrop()
                    .into(holder.ivFotoAnimal);
        } else {
            holder.ivFotoAnimal.setVisibility(View.GONE);
        }

        // Foto del usuario
        if (t.getFotoUsuarioUrl() != null && !t.getFotoUsuarioUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(t.getFotoUsuarioUrl())
                    .centerCrop()
                    .into(holder.ivFotoUsuario);
        }

        // Corazón — verificar si ya dio like
        boolean yaLeDioLike = t.getLikesUsuarios() != null &&
                t.getLikesUsuarios().contains(uidActual);

        holder.btnMeGusta.setImageResource(yaLeDioLike ?
                android.R.drawable.btn_star_big_on :
                android.R.drawable.btn_star_big_off);

        holder.btnMeGusta.setOnClickListener(v -> {
            if (t.getLikesUsuarios() != null && t.getLikesUsuarios().contains(uidActual)) {
                Toast.makeText(v.getContext(), "Ya le diste me gusta", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore.getInstance()
                    .collection("testimonios")
                    .document(t.getId())
                    .update(
                            "meGusta", t.getMeGusta() + 1,
                            "likesUsuarios", FieldValue.arrayUnion(uidActual)
                    )
                    .addOnSuccessListener(a -> {
                        t.setMeGusta(t.getMeGusta() + 1);
                        if (t.getLikesUsuarios() != null) t.getLikesUsuarios().add(uidActual);
                        holder.tvContadorMeGusta.setText(String.valueOf(t.getMeGusta()));
                        holder.btnMeGusta.setImageResource(android.R.drawable.btn_star_big_on);
                    });
        });

        // Botón borrar
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
    public int getItemCount() { return lista.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreUsuario, tvComentario, tvFecha, tvContadorMeGusta;
        ImageButton btnMeGusta, btnBorrar;
        ImageView ivFotoAnimal, ivFotoUsuario;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreUsuario = itemView.findViewById(R.id.tvNombreUsuario);
            tvComentario = itemView.findViewById(R.id.tvComentario);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvContadorMeGusta = itemView.findViewById(R.id.tvContadorMeGusta);
            btnMeGusta = itemView.findViewById(R.id.btnMeGusta);
            btnBorrar = itemView.findViewById(R.id.btnBorrar);
            ivFotoAnimal = itemView.findViewById(R.id.ivFotoAnimal);
            ivFotoUsuario = itemView.findViewById(R.id.ivFotoUsuario);
            ratingBar = itemView.findViewById(R.id.ratingBarTestimonio);
        }
    }
}