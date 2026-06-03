package com.edu.udistrital.backend.competidor.repository;

import com.edu.udistrital.backend.competidor.model.CompetidorDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

//Repository, encargada de la comunicación con la BD
public interface CompetidorRepository extends JpaRepository<CompetidorDTO, Long>{

    /**
     * Busca un competidor por su número de identificación (cédula)
     * Usado para validar duplicados al crear un competidor
     */
    Optional<CompetidorDTO> findByIdentificacion(int identificacion);

    /**
     * Busca la lista de competidores que pertenecen a un género
     */
    List<CompetidorDTO> findByGenero(String genero);

    /**
     * Busca la lista de competidores que pertenecen a una categoría
     */
    List<CompetidorDTO> findByCategoria(String categoria);

    /**
     * Busca la lista de competidores según su especialidad
     */
    List<CompetidorDTO> findByEspecialidad(String especialidad);

    /**
     * Busca la lista de competidores según si participan en modalidad cross o no
     */
    List<CompetidorDTO> findByModalidadCross(Boolean modalidadCross);
}
