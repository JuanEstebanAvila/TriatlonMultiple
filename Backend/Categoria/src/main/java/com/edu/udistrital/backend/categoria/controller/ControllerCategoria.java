package com.edu.udistrital.backend.categoria.controller;

/**
 * Clase controller de tipo @RestController que contiene los endpoints y que delega la parte lógica al service
 */

import com.edu.udistrital.backend.categoria.model.CategoriaDTO;
import com.edu.udistrital.backend.categoria.model.CategoriaResponse;
import com.edu.udistrital.backend.categoria.service.ServiceCategoriaImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
//Raíz en común de todos los endpoints
@RequestMapping("/api/categoria")
public class ControllerCategoria {

    private ServiceCategoriaImpl service;

    //Inyección por medio de constructor del service
    public ControllerCategoria(ServiceCategoriaImpl service){
        this.service = service;
    }

    /**
     * Método de tipo POST para crear una categoría
     * @param datosCategoria
     * @return
     */
    @RequestMapping(value = "/crearcategoria", method = RequestMethod.POST)
    //Mapea lo proveniente de la URL en un objeto de tipo CategoriaDTO y valida sus campos
    public ResponseEntity<?> crearCategoria (@Valid @RequestBody CategoriaDTO datosCategoria){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(service.crearCategoria(datosCategoria));
            //Si la petición resulta exitosa se muestra el código 201
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            //Si la petición no tiene feliz termino se muestra el código 400 (indicando que la petición esta mal formada)
        }
    }

    /**
     * Método tipo PATCH que modifica la descripción de una categoría
     * @param id
     * @param cuerpo
     * @return
     */
    //Uso de PATCH por ser más práctico que POST
    @RequestMapping( value = "/modificardescripcion/{id}", method = RequestMethod.PATCH)
    //Se identifica la categoria a modificar con el id y en el cuerpo viene la nueva descripción
    public ResponseEntity<?> modificarDescripcion(@PathVariable Long id, @RequestBody Map<String, String> cuerpo){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(service.modDescripcion(id, cuerpo.get("descripcion")));
            //Si fue exitoso el cambio se muestra el código de éxito 200
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            //Si la petición fallá se muestra el código de error 404
        }
    }

    /**
     * Método de tipo PATCH que modifica la recomendación de una categoría
     * @param id
     * @param cuerpo
     * @return
     */
    @RequestMapping( value = "/modificarrecomendacion/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<?> modificarRecomendacion(@PathVariable Long id, @RequestBody Map<String, String> cuerpo){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(service.modRecomendacion(id, cuerpo.get("recomendacion"))); //Delega al service y devuelve 200
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //Devuelve 404 si hay error
        }
    }

    /**
     * Método de tipo GET para consultar una categoría por su id
     * @param id
     * @return categoria
     */
    @RequestMapping(value = "/consultarcategoria/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> consultarCategoria(@PathVariable Long id){
        CategoriaResponse categoria = service.buscarCategoria(id);
        return ResponseEntity.status(HttpStatus.OK).body(categoria); //200 y la categoria consultada si todo sale bien
    }

    /**
     * Método de tipo GET para consultar todas las categorías almacenadas
     * @return
     */
    @RequestMapping(value = "/consultartodas", method = RequestMethod.GET)
    public ResponseEntity<?> consultarTodas(){
        return ResponseEntity.status(HttpStatus.OK).body(service.buscarTodas());
    }

    /**
     * Método tipo GET para consultar todas las carreras que registran una categoría,
     * esta consulta incorpora al API "Carrera"
     * @param id
     * @return
     */
    @RequestMapping(value = "/consultarcarreras/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> consultarCarreras(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(service.consultarCarreras(id));
    }

    /**
     * Método tipo DELETE para eliminar una categoría
     * @param id
     * @return
     */
    @RequestMapping(value = "/eliminar/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long id){ //Toma el id de la categoria a eliminar que viene de la url
        return ResponseEntity.status(HttpStatus.OK).body(service.eliminarCategoria(id)); //200 y delegación al service para que elimine la categoria
    }

    /**
     * Método tipo DELETE que elimina una carrera de una categoría (afecta al API "Carrera")
     * @param idCategoria
     * @param idCarrera
     * @return
     */
    @RequestMapping(value = "/{idCategoria}/eliminarcarrera/{idCarrera}", method = RequestMethod.DELETE)
    public ResponseEntity<?> eliminarCarrera(@PathVariable("idCategoria") Long idCategoria, @PathVariable("idCarrera") Long idCarrera){
        return ResponseEntity.status(HttpStatus.OK).body(service.eliminarCarrera(idCategoria, idCarrera));
    }

    /**
     * Método tipo PATCH que recibe la llamada del API "Carrera" para agregar una carrera a la categoría
     * @param idCategoria
     * @param idCarrera
     * @return
     */
    @RequestMapping(value = "/{idCategoria}/agregarcarrera/{idCarrera}", method = RequestMethod.PATCH)
    public ResponseEntity<?> agregarCarrera(@PathVariable("idCategoria") Long idCategoria, @PathVariable("idCarrera") Long idCarrera){
        return ResponseEntity.status(HttpStatus.OK).body(service.agregarCarrera(idCategoria, idCarrera));
    }

}
