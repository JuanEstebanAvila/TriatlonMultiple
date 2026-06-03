package com.edu.udistrital.backend.competidor.model;

//Clase de respuesta hacia el Front y el usuario final, una clase POJO

public class CompetidorResponse {

    private String nombre;

    private Integer edad;

    private String genero;

    private Integer identificacion;

    private String correo;

    private String foto;

    private Boolean modalidadCross;

    private String categoria;

    private String especialidad;

    private CarreraResponse carreraResponse;
    private Long id;

    //Getts y Setts

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Boolean getModalidadCross() {
        return modalidadCross;
    }

    public void setModalidadCross(Boolean modalidadCross) {
        this.modalidadCross = modalidadCross;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public CarreraResponse getCarreraResponse() {
        return carreraResponse;
    }

    public void setCarreraResponse(CarreraResponse carreraResponse) {
        this.carreraResponse = carreraResponse;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(int identificacion) {
        this.identificacion = identificacion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
