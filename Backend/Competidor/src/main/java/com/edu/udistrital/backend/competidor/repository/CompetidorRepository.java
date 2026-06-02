package com.edu.udistrital.backend.competidor.repository;

import com.edu.udistrital.backend.competidor.model.CompetidorDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//Repository, encargada de la comunicación con la BD
public interface CompetidorRepository extends JpaRepository<CompetidorDTO, Long>{

    List<CompetidorDTO> findByEdad(int edad);

    List<CompetidorDTO> findByEspecialidad(String especialidad);

    List<CompetidorDTO> findByModalidadCross(Boolean modalidadCross);
}
