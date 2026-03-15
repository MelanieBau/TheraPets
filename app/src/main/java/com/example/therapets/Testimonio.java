package com.example.therapets;

public class Testimonio {

    private String id;
    private String usuarioId;
    private String nombreUsuario;
    private String comentario;
    private String fecha;
    private long meGusta;
    private String fotoUrl;

    // Constructor vacío necesario para Firestore
    public Testimonio() {}

    // Getters y Setters
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
}