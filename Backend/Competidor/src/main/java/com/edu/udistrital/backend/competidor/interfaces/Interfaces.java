package com.edu.udistrital.backend.competidor.interfaces;

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

  public CompetidorResponse modIdentificacion(Long id, String nuevaIdentificacion);

  public CompetidorResponse modCategoriaEdad(Long id, String nuevaCategoria);

  public CompetidorResponse modGenero(Long id, String nuevoGenero);

  public CompetidorResponse buscarCompetidor(Long id);

  public List<CompetidorResponse> buscarGenero(String genero);

  public List<CompetidorResponse> buscarEdad(String edad);

  public List<CompetidorResponse> buscarEspecialidad(String especialidad);

  public List<CompetidorResponse> buscarCross(Boolean cross);
}
