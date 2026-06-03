package com.edu.udistrital.backend.categorias.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entidad que representa una categoría de triatlón.
 * las categorías clasifican las carreras por distancia,
 * modalidad o grupo de edad.
 * @author julian
 * @version 1.0
 */

@Entity

@Data
@Table(name = "categoria")
@NoArgsConstructor
@AllArgsConstructor

public class Categoria {
    /**
     * Identificador único que se genera automáticamente
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de la categoría. Ej: "Sprint", "Olímpica"
     */
    @Column(nullable = false, name = "nombre")
    @NotBlank (message = "nombre es obligatorio")
    private String nombre;

    /**
     * Tipo de categoría. Ej: "Por distancia", "Por edad"
     */
    @Column(nullable = false, name = "tipo")
    @NotBlank (message = "el tipo es obligatorio")
    private String tipo;

    /**
     * Descripción detallada de la categoría
     */
    @Column(name = "descripcion")
    private String descripcion;

    /**
     * Recomendación para esta categoría
     */
    @Column (name = "recomendacion")
    private String recomendacion;
}
