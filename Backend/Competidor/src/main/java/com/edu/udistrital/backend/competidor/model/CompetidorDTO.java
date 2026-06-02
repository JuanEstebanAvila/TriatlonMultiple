package com.edu.udistrital.backend.competidor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity //Clase que representa la entidad "Competidor"
@Data
@EqualsAndHashCode(callSuper = true) //Incluye los campos del padre "Persona"
@NoArgsConstructor
@Table(name = "competidores")
public class CompetidorDTO extends Persona{

    @Id //Llave primaría de la base de datos
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Autoincremental
    private int id;

    //La URL de la foto
    @NotBlank(message = "La foto no puede estar vacía")
    @Column(name = "foto", length = 120, nullable = false)
    private String foto;

    //Si participa o no en la modalidad cross
    @NotNull(message = "Seleccione si participa o no en modalidad cross")
    @Column(name = "modalidad_cross", nullable = false)
    private Boolean modalidadCross;

    //La categoría a la cual pertenece el competidor
    @NotBlank(message = "Debe pertenecer a alguna categoria")
    @Column(name = "categoria", lenght = 25, nullable = false)
    private String categoria;

    //La especialidad del competidor en el triatlón
    @NotBlank(message = "Seleccione su especialidad en el triatlón")
    @Column(name = "especialidad", nullable = false, length = 30)
    private String especialidad;

    @Column(name = "carrera")
    private String carrera;

    //Constructor que no tiene la llave primaria "id"
    public CompetidorDTO(String nombre, int edad, String genero, int identificacion, String correo, String foto, Boolean modalidadCross, String categoria, String especialidad, String carrera) {
        super(nombre, edad, genero, identificacion, correo);
        this.foto = foto;
        this.modalidadCross = modalidadCross;
        this.categoria = categoria;
        this.especialidad = especialidad;
        this.carrera = carrera;
    }
}
