package com.edu.udistrital.backend.carrera.controller;

/**
 * Clase controller de tipo @RestController que contiene los endpoints y que delega la parte lógica al service
 */

import com.edu.udistrital.backend.carrera.model.CarreraDTO;
import com.edu.udistrital.backend.carrera.model.CarreraResponse;
import com.edu.udistrital.backend.carrera.service.ServiceCarreraImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
//Raíz en común de todos los endpoints
@RequestMapping("/api/carrera")
public class ControllerCarrera {

    private ServiceCarreraImpl service;

    //Inyección por medio de constructor del service
    public ControllerCarrera(ServiceCarreraImpl service){
        this.service = service;
    }

    /**
     * Método de tipo POST para crear una carrera
     * @param datosCarrera
     * @return
     */
    @RequestMapping(value = "/crearcarrera", method = RequestMethod.POST)
    //Mapea lo proveniente de la URL en un objeto de tipo CarreraDTO y valida sus campos
    public ResponseEntity<?> crearCarrera (@Valid @RequestBody CarreraDTO datosCarrera){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(service.crearCarrera(datosCarrera));
            //Si la petición resulta exitosa se muestra el código 201
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            //Si la petición no tiene feliz termino se muestra el código 400 (indicando que la petición esta mal formada)
        }
    }

    /**
     * Método tipo PATCH que modifica la ubicación de una carrera
     * @param id
     * @param cuerpo
     * @return
     */
    //Uso de PATCH por ser más práctico que POST
    @RequestMapping( value = "/modificarubicacion/{id}", method = RequestMethod.PATCH)
    //Se identifica la carrera a modificar con el id y en el cuerpo viene la nueva ubicación
    public ResponseEntity<?> modificarUbicacion(@PathVariable Long id, @RequestBody Map<String, String> cuerpo){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(service.modUbicacion(id, cuerpo.get("ubicacion")));
            //Si fue exitoso el cambio se muestra el código de éxito 200
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            //Si la petición fallá se muestra el código de error 404
        }
    }

    /**
     * Método de tipo PATCH que modifica la fecha de ejecución de una carrera
     * @param id
     * @param cuerpo
     * @return
     */
    @RequestMapping( value = "/modificarfecha/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<?> modificarFecha(@PathVariable Long id, @RequestBody Map<String, Integer> cuerpo){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(service.modFecha(id, cuerpo.get("fechaEjecucion"))); //Delega al service y devuelve 200
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //Devuelve 404 si hay error
        }
    }

    /**
     * Método de tipo GET para consultar una carrera por su id
     * @param id
     * @return carrera
     */
    @RequestMapping(value = "/consultarcarrera/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> consultarCarrera(@PathVariable Long id){
        CarreraResponse carrera = service.buscarCarrera(id);
        return ResponseEntity.status(HttpStatus.OK).body(carrera); //200 y la carrera consultada si todo sale bien
    }

    /**
     * Método tipo GET para consultar todos los competidores inscritos en una carrera,
     * esta consulta incorpora al API "Competidor"
     * @param id
     * @return
     */
    @RequestMapping(value = "/consultarcompetidores/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> consultarCompetidores(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(service.consultarCompetidores(id));
    }

    /**
     * Método tipo GET que consulta la categoría a la que pertenece una carrera,
     * esta consulta incorpora al API "Categoria"
     * @param id
     * @return
     */
    @RequestMapping(value = "/consultarcategoria/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> consultarCategoria(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(service.consultarCategoria(id));
    }

    /**
     * Método tipo DELETE para eliminar una carrera
     * @param id
     * @return
     */
    @RequestMapping(value = "/eliminar/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> eliminarCarrera(@PathVariable Long id){ //Toma el id de la carrera a eliminar que viene de la url
        return ResponseEntity.status(HttpStatus.OK).body(service.eliminarCarrera(id)); //200 y delegación al service para que elimine la carrera
    }

    /**
     * Método tipo DELETE que elimina un competidor de una carrera (afecta al API "Competidor")
     * @param idCarrera
     * @param idCompetidor
     * @return
     */
    @RequestMapping(value = "/{idCarrera}/eliminarcompetidor/{idCompetidor}", method = RequestMethod.DELETE)
    public ResponseEntity<?> eliminarCompetidor(@PathVariable("idCarrera") Long idCarrera, @PathVariable("idCompetidor") Long idCompetidor){
        return ResponseEntity.status(HttpStatus.OK).body(service.eliminarCompetidor(idCarrera, idCompetidor));
    }

    /**
     * Método tipo PATCH que recibe la llamada del API "Competidor" para agregar un competidor a la carrera
     * @param idCarrera
     * @param idCompetidor
     * @return
     */
    @RequestMapping(value = "/{idCarrera}/agregarcompetidor/{idCompetidor}", method = RequestMethod.PATCH)
    public ResponseEntity<?> agregarCompetidor(@PathVariable("idCarrera") Long idCarrera, @PathVariable("idCompetidor") Long idCompetidor){
        return ResponseEntity.status(HttpStatus.OK).body(service.agregarCompetidor(idCarrera, idCompetidor));
    }

}
