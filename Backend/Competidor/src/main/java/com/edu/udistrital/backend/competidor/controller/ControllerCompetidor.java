package com.edu.udistrital.backend.competidor.controller;

/**
 * Clase controller de tipo @RestController que contiene los endpoints y que delega la parte lógica al service
 */

import com.edu.udistrital.backend.competidor.model.CompetidorDTO;
import com.edu.udistrital.backend.competidor.service.ServiceCompetidor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
//Raíz en común de todos los endpoints
@RequestMapping("/api/competidor")
public class ControllerCompetidor {

    private ServiceCompetidor service;

    //Inyección por medio de constructor del service
    public ControllerCompetidor(ServiceCompetidor service){
        this.service = service;
    }

    /**
     * Método de tipo POST para crear un competidor y que su vez envía un correo de confirmación al usuario
     * @param datosCompetidor
     * @return
     */
    @RequestMapping(value = "/crearcompetidor", method = RequestMethod.POST)
    //Mapea lo proveniente de la URL en un objeto de tipo CompetidorDTO y valida sus campos
    public ResponseEntity<?> crearCompetidor (@Valid  @RequestBody CompetidorDTO datosCompetidor){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(datosCompetidor));
            //Si la petición resulta exitosa se muestra el código 200
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            //Si la petición no tiene feliz termino se muestra el código 400 (indicando que la petición esta mal formada)
        }
    }

    /**
     * Método tipo PATCH que modifica solo el atributo "nombre" de un competidor
     * @param id
     * @param cuerpo
     * @return
     */
    //Uso de PATCH por ser más práctico que POST
    @RequestMapping( value = "/modificarnombre/{id}", method = RequestMethod.PATCH)
    //Se identifica el competidor a modificar con el id (que es guardado en una variable de tipo Long)
    //En el cuerpo de la petición viene el nuevo nombre del competidor
    public ResponseEntity<?> modificarNombre(@PathVariable("id") Long id, @RequestBody Map<String, String> cuerpo){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(service.modNombre(id, cuerpo.get("nombre")));
            //Si fue exitoso el cambio se muestra el código de éxito 200
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            //Si la petición fallá se muestra el código de error 404
        }
    }

    /**
     * Método de tipo PATCH que modifica la identificación de un competidor
     * @param id
     * @param cuerpo
     * @return
     */
    @RequestMapping( value = "/modificaridentificacion/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<?> modificarIdentificacion(@PathVariable("id") Long id, @RequestBody Map<String, String> cuerpo){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(service.modIdentificacion(id, cuerpo.get("identificacion"))); //Delega al service y devuelve 200
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //Devuelve 404 si hay error
        }
    }

    /**
     * Método de tipo PATCH que modifica la categoría por edad de un competidor
     * @param id
     * @param cuerpo
     * @return
     */
    @RequestMapping( value = "/modificarcategoriaedad/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<?> modificarCategoriaEdad(@PathVariable("id") Long id, @RequestBody Map<String, String> cuerpo){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(service.modCategoriaEdad(id, cuerpo.get("categoriaEdad")));//Devuelve 200 y delega al serice
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());//404 si hay algún error
        }
    }


    /**
     * Método de tipo PATCH que modifica el género de un competidor
     * @param id
     * @param cuerpo
     * @return
     */
    @RequestMapping( value = "/modificargenero/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<?> modificarGenero(@PathVariable("id") Long id, @RequestBody Map<String, String> cuerpo){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(service.modGenero(id, cuerpo.get("genero")));
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Método de tipo GET para consultar un competidor por su id
     * @param id
     * @return competidor
     */
    @RequestMapping(value = "/consultarcompetidor/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> consultarCompetidor(@PathVariable("id") Long id){
            CompetidorDTO competidor = service.buscarCompetidor(id);
            return ResponseEntity.status(HttpStatus.OK).body(competidor); //200 y el competidor consultado su todo sale bien
    }


    /**
     * Método tipo GET para consultar toda la lista de competidores según su genero
     * @param genero
     * @return
     */
    @RequestMapping(value = "/consultargenero", method = RequestMethod.GET)
    //RequestParam captura el parametro que viene después del ?
    public ResponseEntity<?> consultarListaGenero(@RequestParam("genero") String genero){
        return ResponseEntity.status(HttpStatus.OK).body(service.buscarGenero(genero));
    }

    /**
     * Método tipo GET para consultar la lista de competidores por categoria según edad
     * @param edad
     * @return
     */
    @RequestMapping(value = "/consultarcategoria", method = RequestMethod.GET)
    public ResponseEntity<?> consultarListaGenero(@RequestParam("edad") String edad){
        return ResponseEntity.status(HttpStatus.OK).body(service.buscarListaCategoria(edad));
    }

    /**
     * Método tipo GET para consultar la lista de competidores por especialidad
     * @param especialidad
     * @return
     */
    @RequestMapping(value = "/consultarespecialidad", method = RequestMethod.GET)
    public ResponseEntity<?> consultarEspecialidad(@RequestParam("especialidad") String especialidad){
        return ResponseEntity.status(HttpStatus.OK).body(service.buscarEspecialidad(especialidad));
    }

    /**
     * Método tipo GET para consultar la lista de competidores que esten en modalidad Cross
     * @param cross
     * @return
     */
    @RequestMapping(value = "/consultarcross", method = RequestMethod.GET)
    public ResponseEntity<?> consultarCross(@RequestParam("cross") Boolean cross){
        return ResponseEntity.status(HttpStatus.OK).body(service.buscarCross(cross));
    }

    /**
     * Método tipo DELETE para eliminar un competidor
     * @param id
     * @return
     */
    @RequestMapping(value = "/eliminar/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> eliminarCompetidor(@PathVariable("id")Long id){ //Toma el id del competidor a eliminar que viene de la url
        return ResponseEntity.status(HttpStatus.OK).body(service.eliminarCompetidor(id)); //200 y delegación al service para que elimine al competidor
    }

    /**
     * Método tipo PATCH que registra un competidor en una carrera (este registro necesito de Carrera para hacer el registro)
     * @param id
     * @param cuerpo
     * @return
     */
    @RequestMapping(value = "/registrarcarrera/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<?> registrarCarrera(@PathVariable("id")Long id, @RequestBody Map<String, String> cuerpo){
        return ResponseEntity.status(HttpStatus.OK).body(service.registrarCarrera(cuerpo.get("Carrera")));
    }


    /**
     * Método tipo que GET que consulta la carrera a la que pertenece un competidor identificado con un id,
     * esta consulta incorpora al API "Carrera"
     * @param id
     * @return
     */
    @RequestMapping(value = "/consultarcarrera/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> consultarCarrera(@PathVariable("id") Long id){
        return ResponseEntity.status(HttpStatus.OK).body(service.consultarCarrera(id));
    }












}
