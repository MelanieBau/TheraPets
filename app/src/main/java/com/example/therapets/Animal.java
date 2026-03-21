package com.example.therapets;

public class Animal {

    private String id;
    private String nombre;
    private String tipo;
    private String centro;

    // Constructor vacío necesario para Firestore
    public Animal() {}

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getCentro() { return centro; }
    public void setCentro(String centro) { this.centro = centro; }
}