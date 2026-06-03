package com.edu.udistrital.backend.carrera.model;

import java.util.List;

//Clase de respuesta hacia el Front y el usuario final, una clase POJO

public class CarreraResponse {

    private Long id;

    private String nombreCarrera;

    private String ubicacion;

    private Integer fechaEjecucion;

    private String nivelDificultad;

    private String paraQuien;

    private Long idCategoria;

    private List<Long> competidores;

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

    public String getNivelDificultad() {
        return nivelDificultad;
    }

    public void setNivelDificultad(String nivelDificultad) {
        this.nivelDificultad = nivelDificultad;
    }

    public String getParaQuien() {
        return paraQuien;
    }

    public void setParaQuien(String paraQuien) {
        this.paraQuien = paraQuien;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public List<Long> getCompetidores() {
        return competidores;
    }

    public void setCompetidores(List<Long> competidores) {
        this.competidores = competidores;
    }
}
