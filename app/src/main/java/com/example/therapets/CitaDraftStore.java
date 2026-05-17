package com.example.therapets;

public class CitaDraftStore {
    public static String nombres = "";
    public static String telefono = "";
    public static String fecha = "";
    public static String hora = "";
    public static String centro = "";
    public static String cuidador = "";
    public static String motivo = "";
    public static String otroMotivo = "";
    public static String nombreTerapeuta;

    public static void clear() {
        nombres = telefono = fecha = hora = "";
        centro = "";
        cuidador = "";
        motivo = "";
        otroMotivo = "";
        nombreTerapeuta = "";
    }
}