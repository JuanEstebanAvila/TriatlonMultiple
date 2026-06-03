package com.edu.udistrital.backend.categoria.interfaces;

import com.edu.udistrital.backend.categoria.model.CarreraResponse;
import com.edu.udistrital.backend.categoria.model.CategoriaDTO;
import com.edu.udistrital.backend.categoria.model.CategoriaResponse;

import java.util.List;

/**
 * Clase que establece los contratos entre las diferentes clases
 */

public interface Interfaces {

  public CategoriaResponse crearCategoria(CategoriaDTO datosCategoria);

  public CategoriaResponse modDescripcion(Long id, String nuevaDescripcion);

  public CategoriaResponse modRecomendacion(Long id, String nuevaRecomendacion);

  public CategoriaResponse buscarCategoria(Long id);

  public List<CategoriaResponse> buscarTodas();

  public List<CarreraResponse> consultarCarreras(Long id);

  public String eliminarCategoria(Long id);

  public String eliminarCarrera(Long idCategoria, Long idCarrera);

  public CategoriaResponse agregarCarrera(Long idCategoria, Long idCarrera);
}
