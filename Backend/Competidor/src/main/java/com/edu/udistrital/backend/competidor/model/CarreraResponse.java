package com.edu.udistrital.backend.competidor.model;

//Clase que represneta los atributos de la API "Carrera"

public class CarreraResponse {

    private String nivelDificultad;
    private String ubicacion;
    private int fechaEjecucion;
    private String paraQuien;
    private String nombreCarrera;
    private Long id;

    //Getts y Setts
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCarrera() {
        return nombreCarrera;
    }

    public void setNombreCarrera(String nombreCarrera) {
        this.nombreCarrera = nombreCarrera;
    }

    public String getParaQuien() {
        return paraQuien;
    }

    public void setParaQuien(String paraQuien) {
        this.paraQuien = paraQuien;
    }

    public String getNivelDificultad() {
        return nivelDificultad;
    }

    public void setNivelDificultad(String nivelDificultad) {
        this.nivelDificultad = nivelDificultad;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public int getFechaEjecucion() {
        return fechaEjecucion;
    }

    public void setFechaEjecucion(int fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }

    //Constructor vacío
    public CarreraResponse() {
    }

    //Contructor
    public CarreraResponse(Long id, String nombreCarrera, String paraQuien, int fechaEjecucion, String ubicacion, String nivelDificultad) {
        this.id = id;
        this.nombreCarrera = nombreCarrera;
        this.paraQuien = paraQuien;
        this.fechaEjecucion = fechaEjecucion;
        this.ubicacion = ubicacion;
        this.nivelDificultad = nivelDificultad;
    }
}
