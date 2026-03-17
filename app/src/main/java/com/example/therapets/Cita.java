package com.example.therapets;

public class Cita {

    private String id;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String fecha;
    private String hora;
    private String centro;
    private String cuidador;
    private String motivo;
    private String estado;
    private String usuarioId;

    // Constructor vacío necesario para Firestore
    public Cita() {}

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getCentro() { return centro; }
    public void setCentro(String centro) { this.centro = centro; }

    public String getCuidador() { return cuidador; }
    public void setCuidador(String cuidador) { this.cuidador = cuidador; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
}