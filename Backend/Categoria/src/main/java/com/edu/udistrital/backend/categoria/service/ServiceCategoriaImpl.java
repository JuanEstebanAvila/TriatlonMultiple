package com.edu.udistrital.backend.categoria.service;

import com.edu.udistrital.backend.categoria.interfaces.Interfaces;
import com.edu.udistrital.backend.categoria.model.CarreraResponse;
import com.edu.udistrital.backend.categoria.model.CategoriaDTO;
import com.edu.udistrital.backend.categoria.model.CategoriaResponse;
import com.edu.udistrital.backend.categoria.repository.CategoriaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase Service, encargada de implementar toda la lógica de negocio y por lo tanto de los endpoints
 */
@Service
public class ServiceCategoriaImpl implements Interfaces{

    //Inyección por constructor de las dependencias
    private final CategoriaRepository repository;
    private final ModelMapper modelMapperBean;
    private final WebClient webClientBean;

    public ServiceCategoriaImpl(CategoriaRepository repository, ModelMapper modelMapperBean, WebClient webClientBean){
        this.repository = repository;
        this.modelMapperBean = modelMapperBean;
        this.webClientBean = webClientBean;
    }

    /**
     * Delega al Repository para que guarde una nueva categoría con los datos que llegan del Front
     * @param datosCategoria
     * @return
     */
    @Override
    public CategoriaResponse crearCategoria(CategoriaDTO datosCategoria){
        CategoriaDTO creada = repository.save(datosCategoria);
        return modelMapperBean.map(creada, CategoriaResponse.class);
    }

    /**
     * Método para modificar la descripción de una categoría
     * @param id
     * @param nuevaDescripcion
     * @return
     */
    @Override
    public CategoriaResponse modDescripcion(Long id, String nuevaDescripcion){
        CategoriaDTO categoria = repository.findById(id)
                .orElseThrow(()-> new RuntimeException("No existe una categoria con el id:" + id));

        //Si todo sale bien cambia la descripción y guarda el cambio
        categoria.setDescripcion(nuevaDescripcion);
        CategoriaDTO guardada = repository.save(categoria);

        return modelMapperBean.map(guardada, CategoriaResponse.class);
    }

    /**
     * Método que modifica la recomendación de una categoría
     * @param id
     * @param nuevaRecomendacion
     * @return
     */
    @Override
    public CategoriaResponse modRecomendacion(Long id, String nuevaRecomendacion){
        CategoriaDTO categoria = repository.findById(id)
                .orElseThrow(()-> new RuntimeException("No existe una categoria con id " + id));

        categoria.setRecomendacion(nuevaRecomendacion);
        CategoriaDTO guardada = repository.save(categoria);

        return modelMapperBean.map(guardada, CategoriaResponse.class);
    }

    /**
     * Método que busca una categoría por su id y la retorna como un objeto de tipo CategoriaResponse
     * @param id
     * @return
     */
    @Override
    public CategoriaResponse buscarCategoria(Long id){
        CategoriaDTO categoria = repository.findById(id) //Primero se busca en la base de datos
                .orElseThrow(() -> new RuntimeException("No existe una categoria con id " + id )); //Corroboramos que exista

        return modelMapperBean.map(categoria, CategoriaResponse.class); //Si todo esta bien, se mapea a un objeto de tipo CategoriaResponse
    }

    /**
     * Método que consulta todas las categorías almacenadas en la base de datos
     * @return
     */
    @Override
    public List<CategoriaResponse> buscarTodas(){
        List<CategoriaDTO> categorias = repository.findAll(); //Trae todas las categorias de la BD
        List<CategoriaResponse> listaRespuesta = new ArrayList<>(); //Lista que se va a retornar

        //Recorremos cada categoria y la mapeamos a un CategoriaResponse
        for(CategoriaDTO categoria : categorias){
            listaRespuesta.add(modelMapperBean.map(categoria, CategoriaResponse.class));
        }
        return listaRespuesta;
    }

    /**
     * Método que consulta todas las carreras que pertenecen a una categoría,
     * involucra 2 APIS, API "categoria" y API "carrera"
     * @param id
     * @return
     */
    @Override
    public List<CarreraResponse> consultarCarreras(Long id){
        //Primero extraemos la categoría y verificamos que exista
        CategoriaDTO categoria = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe una categoria con id " + id));

        List<CarreraResponse> listaCarreras = new ArrayList<>(); //Lista que se va a retornar

        //Recorremos los id de las carreras que pertenecen a la categoria
        for(Long idCarrera : categoria.getCarreras()){
            //Por cada id hacemos una petición al API "Carrera" para traer sus datos
            CarreraResponse carrera = webClientBean.get()
                    .uri("/consultarcarrera/{id}", idCarrera)
                    .retrieve()
                    .bodyToMono(CarreraResponse.class)
                    .block();
            listaCarreras.add(carrera);
        }
        return listaCarreras;
    }

    /**
     * Método para eliminar una categoría por su id
     * @param id
     * @return
     */
    @Override
    public String eliminarCategoria(Long id){
        if(repository.findById(id).isEmpty()) { //Primero se verifica que exista la categoria
            throw new RuntimeException("No se puede eliminar una categoria que no existe");
        }
        repository.deleteById(id); //Después de corroborar se delega al repository para que la elimine

        return ("La categoria con id " + id + " fue eliminada exitosamente");
    }

    /**
     * Método que elimina una carrera de una categoría,
     * involucra 2 APIS, API "categoria" y API "carrera"
     * @param idCategoria
     * @param idCarrera
     * @return
     */
    @Override
    public String eliminarCarrera(Long idCategoria, Long idCarrera){
        //Extraemos la categoría y verificamos que exista
        CategoriaDTO categoria = repository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Esa categoria no existe"));

        //Verificamos que la carrera este registrada en la categoria
        if(!categoria.getCarreras().contains(idCarrera)){
            throw new RuntimeException("La carrera no esta registrada en esta categoria");
        }

        categoria.getCarreras().remove(idCarrera); //Se quita el id de la carrera de la lista
        repository.save(categoria);

        //WebClient para avisar al proyecto "Carrera" que quite la categoria de esa carrera
        webClientBean.patch()
                .uri("/{idcarrera}/quitarcategoria", idCarrera)
                .retrieve()
                .bodyToMono(CarreraResponse.class)
                .block();

        return ("La carrera con id " + idCarrera + " fue eliminada de la categoria con id " + idCategoria);
    }

    /**
     * Método que recibe la llamada del proyecto "Carrera" para agregar una carrera a la lista de la categoria
     * @param idCategoria
     * @param idCarrera
     * @return
     */
    @Override
    public CategoriaResponse agregarCarrera(Long idCategoria, Long idCarrera){
        //Extraemos la categoría y verificamos que exista
        CategoriaDTO categoria = repository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Esa categoria no existe"));

        //Si la carrera no esta ya en la lista, se agrega
        if(!categoria.getCarreras().contains(idCarrera)){
            categoria.getCarreras().add(idCarrera);
        }
        CategoriaDTO guardada = repository.save(categoria);

        return modelMapperBean.map(guardada, CategoriaResponse.class);
    }

}
