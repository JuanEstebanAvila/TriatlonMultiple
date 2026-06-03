package com.edu.udistrital.backend.carrera.model;

//Clase que representa los atributos de la categoria que llegan desde la API "Categoria"

public class CategoriaResponse {

    private Long id;
    private String nombreCategoria;
    private String descripcion;
    private String recomendacion;

    //Getts y Setts
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRecomendacion() {
        return recomendacion;
    }

    public void setRecomendacion(String recomendacion) {
        this.recomendacion = recomendacion;
    }
}
