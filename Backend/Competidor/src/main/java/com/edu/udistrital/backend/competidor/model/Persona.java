package com.edu.udistrital.backend.competidor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase padre que sirve para hacer el aplicativo más escalable,
 * al largo plazo pudiendo implementar otras entidades que hereden de esta
 */

@Data //Getts y Setts
@MappedSuperclass //Índica el índole de esta clase como padre
@NoArgsConstructor
public class Persona {

    @NotBlank(message = "El nombre no puede estar vacio") //Blank para textos, no nulos y no espacios
    @Column(name = "nombre", length = 50 ,nullable = false)
    private String nombre;

    @NotNull(message = "La edad no puede estar vacia")//Null para números, no nulos
    @Positive //No hay edades negativas
    @Column(name = "edad", lenght = 20, nullable = false)
    private int edad;

    @NotNull(message = "La identificación no puede estar vacia")
    @Column(name = "identificacion", unique = true, lenght = 30, nullable = false) //Unique hace que sea único en toda la tabla
    private int identificacion;

    @NotBlank(message = "El genero no puede estar vacío")
    @Column(name = "genero", length = 10, nullable = false)
    private String genero;

    @Email(message = "El formato del correo no es valido") //Verifica que contenga el arroba "@" y un dominio
    @Column(name = "correo", length = 40, nullable = false)
    private String correo;

    //Constructor
    public Persona(String nombre, int edad, String genero, int identificacion, String correo) {
        this.nombre = nombre;
        this.edad = edad;
        this.genero = genero;
        this.identificacion = identificacion;
        this.correo = correo;
    }
}
