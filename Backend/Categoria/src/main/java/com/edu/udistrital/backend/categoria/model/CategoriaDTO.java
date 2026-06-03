package com.edu.udistrital.backend.categoria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity //Clase que representa la entidad "Categoria"
@Data
@NoArgsConstructor
@Table(name = "categorias")
public class CategoriaDTO {

    @Id //Llave primaría de la base de datos
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Autoincremental
    private Long id;

    //El nombre de la categoria
    @NotBlank(message = "El nombre de la categoria no puede estar vacio")
    @Column(name = "nombre_categoria", length = 50, nullable = false)
    private String nombreCategoria;

    //El tipo de la categoria (por distancia, por edad/genero, por modalidad/terreno)
    @NotBlank(message = "El tipo de la categoria no puede estar vacio")
    @Column(name = "tipo", length = 50, nullable = false)
    private String tipo;

    //La descripcion de la categoria (distancias de natacion, ciclismo y carrera)
    @NotBlank(message = "La descripcion no puede estar vacia")
    @Column(name = "descripcion", length = 200, nullable = false)
    private String descripcion;

    //La recomendacion de la categoria
    @NotBlank(message = "La recomendacion no puede estar vacia")
    @Column(name = "recomendacion", length = 200, nullable = false)
    private String recomendacion;

    //Lista con los id de las carreras que pertenecen a esta categoria
    @ElementCollection
    @CollectionTable(name = "categoria_carreras", joinColumns = @JoinColumn(name = "id_categoria"))
    @Column(name = "id_carrera")
    private List<Long> carreras = new ArrayList<>();

    //Constructor que no tiene la llave primaria "id"
    public CategoriaDTO(String nombreCategoria, String tipo, String descripcion, String recomendacion) {
        this.nombreCategoria = nombreCategoria;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.recomendacion = recomendacion;
    }
}
