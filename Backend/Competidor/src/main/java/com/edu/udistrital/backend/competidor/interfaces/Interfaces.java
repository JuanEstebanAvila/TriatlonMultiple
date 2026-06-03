package com.edu.udistrital.backend.competidor.interfaces;

import com.edu.udistrital.backend.competidor.model.CarreraResponse;
import com.edu.udistrital.backend.competidor.model.CompetidorDTO;
import com.edu.udistrital.backend.competidor.model.CompetidorResponse;

import java.util.List;

/**
 * Clase que establece los contratos entre las diferentes clases
 */

public interface Interfaces {

  public void enviarCorreo(String destinatario, String asunto, String mensaje);

  public CompetidorResponse crearCompetidor(CompetidorDTO datosCompetidor);

  public CompetidorResponse modNombre(Long id, String nombreNuevo);

  public CompetidorResponse modIdentificacion(Long id, int nuevaIdentificacion);

  public CompetidorResponse modCategoriaEdad(Long id, String nuevaCategoria);

  public CompetidorResponse modGenero(Long id, String nuevoGenero);

  public CompetidorResponse buscarCompetidor(Long id);

  public List<CompetidorResponse> buscarGenero(String genero);

  public List<CompetidorResponse> buscarListaCategoria(String categoria);

  public List<CompetidorResponse> buscarEspecialidad(String especialidad);

  public List<CompetidorResponse> buscarCross(Boolean cross);

  public String eliminarCompetidor(Long id);

  public CompetidorResponse registrarCarrera(Long idCompetidor, Long idCarrera);

  public CarreraResponse consultarCarrera(Long id);

  public CompetidorResponse modificarPreferencias(Long id, String nuevaEspecialidad, Boolean nuevaModalidadCross);
}
