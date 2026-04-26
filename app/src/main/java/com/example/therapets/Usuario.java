package com.example.therapets;

public class Usuario {

    private String id;
    private String nombre;
    private String email;
    private String rol;
    private String centroId;

    // Constructor vacío necesario para Firestore
    public Usuario() {}

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getCentroId() { return centroId; }
    public void setCentroId(String centroId) { this.centroId = centroId; }
}