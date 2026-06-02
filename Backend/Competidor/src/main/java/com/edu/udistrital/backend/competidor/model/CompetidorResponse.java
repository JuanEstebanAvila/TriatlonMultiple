package com.edu.udistrital.backend.competidor.model;

//Clase de respuesta hacia el Front y el usuario final, una clase POJO

public class CompetidorResponse {

    private String nombre;

    private int edad;

    private String genero;

    private int identificacion;

    private String correo;

    private String nombre;

    private String foto;

    private oolean modalidadCross;

    private String categoria;

    private String especialidad;

    private String idCarrera;

    private String nivelDificultad;

    private String ubicacion;

    private int fechaEjecucion;

    private String paraQuien;

    private String nombreCarrera;

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

    public oolean getModalidadCross() {
        return modalidadCross;
    }

    public void setModalidadCross(oolean modalidadCross) {
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

    public String getIdCarrera() {
        return idCarrera;
    }

    public void setIdCarrera(String idCarrera) {
        this.idCarrera = idCarrera;
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

    public String getParaQuien() {
        return paraQuien;
    }

    public void setParaQuien(String paraQuien) {
        this.paraQuien = paraQuien;
    }

    public String getNombreCarrera() {
        return nombreCarrera;
    }

    public void setNombreCarrera(String nombreCarrera) {
        this.nombreCarrera = nombreCarrera;
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

    //Constructores

    public CompetidorResponse( ) {
    }

    public CompetidorResponse(String nombre, int edad, String genero, String correo, int identificacion, String nombre1, String foto, oolean modalidadCross, String categoria, String especialidad, String idCarrera, String nivelDificultad, String ubicacion, int fechaEjecucion, String paraQuien, String nombreCarrera, Long id) {
        this.nombre = nombre;
        this.edad = edad;
        this.genero = genero;
        this.correo = correo;
        this.identificacion = identificacion;
        this.nombre = nombre1;
        this.foto = foto;
        this.modalidadCross = modalidadCross;
        this.categoria = categoria;
        this.especialidad = especialidad;
        this.idCarrera = idCarrera;
        this.nivelDificultad = nivelDificultad;
        this.ubicacion = ubicacion;
        this.fechaEjecucion = fechaEjecucion;
        this.paraQuien = paraQuien;
        this.nombreCarrera = nombreCarrera;
        this.id = id;
    }
}
