package com.edu.udistrital.backend.categorias.Controller;

import com.edu.udistrital.backend.categorias.Service.ServiceCategoria;
import com.edu.udistrital.backend.categorias.modelo.Categoria;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de categorías en el sistema
 * <p>
 * Provee los endpoints de la API para realizar operaciones CRUD y modificaciones
 * sobre los recursos de tipo {@link Categoria}
 * </p>
 * Configura CORS para permitir peticiones desde cualquier origen y soporta los métodos
 * HTTP necesarios para la integración con el FrontEnd.
 * @author Julian
 * @version 1.0
 */

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
public class CategoriaController {

    /**
     * Servicio que contiene la lógica de negocio para las categorías.
     */
    private final ServiceCategoria service;

    /**
     * Constructor único para la inyección de dependencias del servicio de categorías
     *
     * @param service el servicio de negocio que será utilizado por el controlador
     */
    public CategoriaController (ServiceCategoria service){
        this.service = service;
    }
    /**
     * Obtiene la lista completa de todas las categorías registradas en el sistema
     *
     * @return una lista con todos los objetos {@link Categoria} encontrados
     */
    //get
    @GetMapping
    public List<Categoria> listarTodas(){
        return service.obtenerTodas();
    }
    /**
     * Crea y almacena una nueva categoría en el sistema
     *
     * @param categoria el objeto {@link Categoria} enviado en el cuerpo de la petición, validado previamente
     * @return la entidad {@link Categoria} persistida con su ID asignado por la base de datos
     */
    //post
    @PostMapping
    public Categoria crear(@Valid @RequestBody Categoria categoria){
        return service.guardar(categoria);
    }

    /**
     * Busca una categoría específica utilizando su identificador único
     *
     * @param id el identificador único de la categoría a consultar
     * @return un {@link ResponseEntity} con estado 200 OK y la categoría si es encontrada,
     * o estado 404 Not Found si el ID no existe en el sistema
     */
    @GetMapping ("/{id}")
    public ResponseEntity<Categoria> buscarPorId (@PathVariable Long id){
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)    //existe 200
                .orElse(ResponseEntity.notFound().build()); //no existe 404
    }

    /**
     * Elimina una categoría del sistema a partir de su identificador único
     *
     * @param id el identificador único de la categoría que se desea remover
     * @return un {@link ResponseEntity} con estado 204 No Content si la eliminación fue exitosa,
     * o estado 404 Not Found si la categoría no existía previamente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar (@PathVariable Long id) {
        boolean eliminado = service.eliminar(id);
        if (!eliminado){
            return ResponseEntity.notFound().build();   //404 si no existia
        }
        return ResponseEntity.noContent().build();     //204 si lo borro
    }
    /**
     * Modifica de manera selectiva la descripción de una categoría existente
     *
     * @param id el identificador único de la categoría a modificar
     * @param nuevaDescripcion el nuevo texto que reemplazará la descripción actual
     * @return un {@link ResponseEntity} con estado 200 OK y la categoría actualizada si la operación tiene éxito,
     * o estado 404 Not Found si el identificador de la categoría no fue hallado
     */
    @PatchMapping("/{id}/descripcion")
    public ResponseEntity <Categoria> modificarDescripcion(@PathVariable Long id, @RequestBody String nuevaDescripcion){
        return service.actualizaDescripcion(id, nuevaDescripcion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Modifica de manera selectiva la recomendación de una categoría existente
     *
     * @param id el identificador único de la categoría a modificar
     * @param nuevaRecomendacion el nuevo texto que reemplazará la recomendación actual
     * @return un {@link ResponseEntity} con estado 200 OK y la categoría actualizada si la operación tiene éxito,
     * o estado 404 Not Found si el identificador de la categoría no fue hallado
     */
    @PatchMapping("/{id}/recomendacion")
    public ResponseEntity <Categoria> modificarRecomendacion(@PathVariable Long id, @RequestBody String nuevaRecomendacion){
        return service.actualizarRecomendacion(id,nuevaRecomendacion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
