package com.edu.udistrital.backend.categorias.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.edu.udistrital.backend.categorias.modelo.Categoria;

/**
 * Repositorio JPA para operaciones CRUD sobre la entidad Categoria.
 * Hereda automáticamente save(), findAll(), findById(), deleteById().
 *
 * @author julian
 * @version 1.0
 */
@Repository
// Al heredar de JpaRepository, ya se tiene .save(), .findAll(), .findById(), .deleteById() automaticamente.
public interface CategoriaRepositorio extends JpaRepository<Categoria, Long> {

}
