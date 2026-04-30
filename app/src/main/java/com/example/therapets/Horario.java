package com.example.therapets;

public class Horario {

    private String id;
    private String centroId;
    private String dia;
    private String hora;

    public Horario() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCentroId() { return centroId; }
    public void setCentroId(String centroId) { this.centroId = centroId; }

    public String getDia() { return dia; }
    public void setDia(String dia) { this.dia = dia; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
}