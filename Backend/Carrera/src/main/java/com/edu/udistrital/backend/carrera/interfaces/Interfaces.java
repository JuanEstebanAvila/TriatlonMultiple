package com.edu.udistrital.backend.carrera.interfaces;

import com.edu.udistrital.backend.carrera.model.CarreraDTO;
import com.edu.udistrital.backend.carrera.model.CarreraResponse;
import com.edu.udistrital.backend.carrera.model.CategoriaResponse;
import com.edu.udistrital.backend.carrera.model.CompetidorResponse;

import java.util.List;

/**
 * Clase que establece los contratos entre las diferentes clases
 */

public interface Interfaces {

  public CarreraResponse crearCarrera(CarreraDTO datosCarrera);

  public CarreraResponse modUbicacion(Long id, String nuevaUbicacion);

  public CarreraResponse modFecha(Long id, int nuevaFecha);

  public CarreraResponse buscarCarrera(Long id);

  public List<CompetidorResponse> consultarCompetidores(Long id);

  public CategoriaResponse consultarCategoria(Long id);

  public String eliminarCarrera(Long id);

  public CarreraResponse eliminarCompetidor(Long idCarrera, Long idCompetidor);

  public CarreraResponse agregarCompetidor(Long idCarrera, Long idCompetidor);
}
