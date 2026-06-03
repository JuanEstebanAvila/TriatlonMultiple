package com.edu.udistrital.backend.carrera.repository;

import com.edu.udistrital.backend.carrera.model.CarreraDTO;
import org.springframework.data.jpa.repository.JpaRepository;

//Repository, encargada de la comunicación con la BD
public interface CarreraRepository extends JpaRepository<CarreraDTO, Long>{

}
