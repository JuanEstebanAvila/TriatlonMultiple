package com.edu.udistrital.backend.carrera.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity //Clase que representa la entidad "Carrera"
@Data
@NoArgsConstructor
@Table(name = "carreras")
public class CarreraDTO {

    @Id //Llave primaría de la base de datos
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Autoincremental
    private Long id;

    //El nombre de la carrera
    @NotBlank(message = "El nombre de la carrera no puede estar vacio")
    @Column(name = "nombre_carrera", length = 50, nullable = false)
    private String nombreCarrera;

    //La ubicación donde se realiza la carrera
    @NotBlank(message = "La ubicacion no puede estar vacia")
    @Column(name = "ubicacion", length = 60, nullable = false)
    private String ubicacion;

    //La fecha de ejecución de la carrera
    @NotNull(message = "La fecha de ejecucion no puede estar vacia")
    @Column(name = "fecha_ejecucion", nullable = false)
    private Integer fechaEjecucion;

    //El nivel de dificultad de la carrera
    @NotBlank(message = "El nivel de dificultad no puede estar vacio")
    @Column(name = "nivel_dificultad", length = 25, nullable = false)
    private String nivelDificultad;

    //Para quien esta diseñada la carrera
    @NotBlank(message = "Debe indicar para quien esta diseñada la carrera")
    @Column(name = "para_quien", length = 50, nullable = false)
    private String paraQuien;

    //El id de la categoria, manejada en el tercer microservicio
    @Column(name = "id_categoria")
    private Long idCategoria;

    //Lista con los id de los competidores inscritos en la carrera
    @ElementCollection
    @CollectionTable(name = "carrera_competidores", joinColumns = @JoinColumn(name = "id_carrera"))
    @Column(name = "id_competidor")
    private List<Long> competidores = new ArrayList<>();

    //Constructor que no tiene la llave primaria "id"
    public CarreraDTO(String nombreCarrera, String ubicacion, int fechaEjecucion, String nivelDificultad, String paraQuien, Long idCategoria) {
        this.nombreCarrera = nombreCarrera;
        this.ubicacion = ubicacion;
        this.fechaEjecucion = fechaEjecucion;
        this.nivelDificultad = nivelDificultad;
        this.paraQuien = paraQuien;
        this.idCategoria = idCategoria;
    }
}
