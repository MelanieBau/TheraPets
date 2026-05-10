package com.example.therapets;

import java.util.List;

public class Testimonio {

    private String id;
    private String usuarioId;
    private String nombreUsuario;
    private String comentario;
    private String fecha;
    private long meGusta;
    private String fotoUrl;
    private String fotoUsuarioUrl;
    private float estrellas;
    private List<String> likesUsuarios;

    public Testimonio() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public long getMeGusta() { return meGusta; }
    public void setMeGusta(long meGusta) { this.meGusta = meGusta; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public String getFotoUsuarioUrl() { return fotoUsuarioUrl; }
    public void setFotoUsuarioUrl(String fotoUsuarioUrl) { this.fotoUsuarioUrl = fotoUsuarioUrl; }

    public float getEstrellas() { return estrellas; }
    public void setEstrellas(float estrellas) { this.estrellas = estrellas; }

    public List<String> getLikesUsuarios() { return likesUsuarios; }
    public void setLikesUsuarios(List<String> likesUsuarios) { this.likesUsuarios = likesUsuarios; }
}