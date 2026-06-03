package com.edu.udistrital.backend.categoria.repository;

import com.edu.udistrital.backend.categoria.model.CategoriaDTO;
import org.springframework.data.jpa.repository.JpaRepository;

//Repository, encargada de la comunicación con la BD
public interface CategoriaRepository extends JpaRepository<CategoriaDTO, Long>{

}
