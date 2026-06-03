package com.edu.udistrital.backend.categorias.Service;


import com.edu.udistrital.backend.categorias.modelo.Categoria;
import com.edu.udistrital.backend.categorias.repositorio.CategoriaRepositorio;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Servicio que contiene la lógica de negocio
 * para la gestión de categorías de triatlón
 *
 * @author julian
 * @version 1.0
 */

@Service
public class ServiceCategoria {

    /**
     * Repositorio para acceso a datos de categorías.
     */
    private final CategoriaRepositorio repository;

    /**
     * Constructor que inyecta el repositorio.
     * @param repository repositorio de categorías
     */
    // El constructor inyecta el repositorio automaticamente
    public ServiceCategoria(CategoriaRepositorio repository){
        this.repository = repository;
    }

    /**
     * Guarda una nueva categoría en la base de datos
     * @param categoria objeto categoría a guardar
     * @return la categoría guardada con su ID generado
     */
    // metodo para guardar una nueva categoria
    public Categoria guardar(Categoria categoria){
        return repository.save(categoria);
    }

    /**
     * Retorna todas las categorías almacenadas.
     * @return lista de categorías
     */
    public List<Categoria> obtenerTodas(){
        return repository.findAll();
    }

    /**
     * Busca una categoría por su id
     * @param id identificador de la categoría
     * @return Optional con la categoría si existe, Optional.empty() si no
     */
    // metodo para buscar una sola categoria por su id
    public Optional<Categoria> obtenerPorId(Long id){
        return repository.findById(id);
    }

    /**
     * Elimina una categoría si existe
     * @param id identificador de la categoría a eliminar
     * @return true si fue eliminada, false si no existía
     */
    // metodo para eliminar una categoria
    public boolean eliminar(Long id){
        if (repository.existsById(id)){
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Actualiza la descripción de una categoría existente
     * @param id identificador de la categoría
     * @param nuevaDescripcion nueva descripción a asignar
     * @return la categoría actualizada o null si no existe
     */
    //modificar descripcion
    public Optional<Categoria> actualizaDescripcion (Long id, String nuevaDescripcion){
        return repository.findById(id).map(categoria -> {
        categoria.setDescripcion(nuevaDescripcion);
        return repository.save(categoria);
        });
        //si no existe optional.empty()
    }

    /**
     * Actualiza la recomendación de una categoría existente
     * @param id identificador de la categoría
     * @param nuevaRecomendacion nueva recomendación a asignar
     * @return la categoría actualizada o null si no existe
     */
    //modificar recomendacion
    public Optional<Categoria> actualizarRecomendacion (Long id, String nuevaRecomendacion){
        return repository.findById(id).map(categoria ->{
            categoria.setRecomendacion(nuevaRecomendacion);
            return repository.save(categoria);
        });
        //empty
    }
}
