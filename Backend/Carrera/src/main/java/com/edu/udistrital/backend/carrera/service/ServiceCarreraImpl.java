package com.edu.udistrital.backend.carrera.service;

import com.edu.udistrital.backend.carrera.interfaces.Interfaces;
import com.edu.udistrital.backend.carrera.model.CarreraDTO;
import com.edu.udistrital.backend.carrera.model.CarreraResponse;
import com.edu.udistrital.backend.carrera.model.CategoriaResponse;
import com.edu.udistrital.backend.carrera.model.CompetidorResponse;
import com.edu.udistrital.backend.carrera.repository.CarreraRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase Service, encargada de implementar toda la lógica de negocio y por lo tanto de los endpoints
 */
@Service
public class ServiceCarreraImpl implements Interfaces{

    //Inyección por constructor de las dependencias
    private final CarreraRepository repository;
    private final ModelMapper modelMapperBean;
    private final WebClient webClientCompetidor;
    private final WebClient webClientCategoria;

    public ServiceCarreraImpl(CarreraRepository repository, ModelMapper modelMapperBean,
                              @Qualifier("webClientCompetidor") WebClient webClientCompetidor,
                              @Qualifier("webClientCategoria") WebClient webClientCategoria){
        this.repository = repository;
        this.modelMapperBean = modelMapperBean;
        this.webClientCompetidor = webClientCompetidor;
        this.webClientCategoria = webClientCategoria;
    }

    /**
     * Delega al Repository para que guarde una nueva carrera con los datos que llegan del Front
     * @param datosCarrera
     * @return
     */
    @Override
    public CarreraResponse crearCarrera(CarreraDTO datosCarrera){
        CarreraDTO creada = repository.save(datosCarrera);

        //Si la carrera tiene categoria, se avisa al proyecto "Categoria" para que la registre en su lista
        if(creada.getIdCategoria() != null){
            webClientCategoria.patch()
                    .uri("/{idCategoria}/agregarcarrera/{idCarrera}", creada.getIdCategoria(), creada.getId())
                    .retrieve()
                    .bodyToMono(CategoriaResponse.class)
                    .block();
        }

        return modelMapperBean.map(creada, CarreraResponse.class);
    }

    /**
     * Método para modificar la ubicación de una carrera
     * @param id
     * @param nuevaUbicacion
     * @return
     */
    @Override
    public CarreraResponse modUbicacion(Long id, String nuevaUbicacion){
        CarreraDTO carrera = repository.findById(id)
                .orElseThrow(()-> new RuntimeException("No existe una carrera con el id:" + id));

        //Si todo sale cambia la ubicación y guarda el cambio
        carrera.setUbicacion(nuevaUbicacion);
        CarreraDTO guardada = repository.save(carrera);

        return modelMapperBean.map(guardada, CarreraResponse.class);
    }

    /**
     * Método que modifica la fecha de ejecución de una carrera
     * @param id
     * @param nuevaFecha
     * @return
     */
    @Override
    public CarreraResponse modFecha(Long id, int nuevaFecha){
        CarreraDTO carrera = repository.findById(id)
                .orElseThrow(()-> new RuntimeException("No existe una carrera con id " + id));

        carrera.setFechaEjecucion(nuevaFecha);
        CarreraDTO guardada = repository.save(carrera);

        return modelMapperBean.map(guardada, CarreraResponse.class);
    }

    /**
     * Método que busca una carrera por su id y la retorna como un objeto de tipo CarreraResponse
     * @param id
     * @return
     */
    @Override
    public CarreraResponse buscarCarrera(Long id){
        CarreraDTO carrera = repository.findById(id) //Primero se busca en la base de datos
                .orElseThrow(() -> new RuntimeException("No existe una carrera con id " + id )); //Corroboramos que exista

        return modelMapperBean.map(carrera, CarreraResponse.class); //Si todo esta bien, se mapea a un objeto de tipo CarreraResponse
    }

    /**
     * Método que consulta todos los competidores inscritos en una carrera,
     * involucra 2 APIS, API "carrera" y API "competidor"
     * @param id
     * @return
     */
    @Override
    public List<CompetidorResponse> consultarCompetidores(Long id){
        //Primero extraemos la carrera y verificamos que exista
        CarreraDTO carrera = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe una carrera con id " + id));

        List<CompetidorResponse> listaCompetidores = new ArrayList<>(); //Lista que se va a retornar

        //Recorremos los id de los competidores inscritos en la carrera
        for(Long idCompetidor : carrera.getCompetidores()){
            //Por cada id hacemos una petición al API "Competidor" para traer sus datos
            CompetidorResponse competidor = webClientCompetidor.get()
                    .uri("/consultarcompetidor/{id}", idCompetidor)
                    .retrieve()
                    .bodyToMono(CompetidorResponse.class)
                    .block();
            listaCompetidores.add(competidor);
        }
        return listaCompetidores;
    }

    /**
     * Método para consultar la categoría a la cual pertenece una carrera,
     * incorpora al API "Categoria"
     * @param id
     * @return
     */
    @Override
    public CategoriaResponse consultarCategoria(Long id){
        CarreraDTO carrera = repository.findById(id) //Busca la carrera en la BD
                .orElseThrow(()-> new RuntimeException("La carrera no existe"));//Si no existe lanza una excepción

        if(carrera.getIdCategoria()==null){ //Comprobamos que la carrera tenga una categoria asignada
            throw new RuntimeException("La carrera no tiene una categoria asignada");
        }

        //Si cumple con lo anterior se hace una petición al API "Categoria"
        CategoriaResponse categoria = webClientCategoria.get()//De tipo consulta
                .uri("/consultarcategoria/{id}", carrera.getIdCategoria())//El endpoint del api "Categoria"
                .retrieve()//Envía la petición
                .bodyToMono(CategoriaResponse.class)//Esperamos que devuelva un objeto de tipo CategoriaResponse
                .block(); //Vuelve Sincrono

        return categoria;
    }

    /**
     * Método para eliminar una carrera por su id
     * @param id
     * @return
     */
    @Override
    public String eliminarCarrera(Long id){
        if(repository.findById(id).isEmpty()) { //Primero se verifica que exista la carrera
            throw new RuntimeException("No se puede eliminar una carrera que no existe");
        }
        repository.deleteById(id); //Después de corroborar se delega al repository para que la elimine

        return ("La carrera con id " + id + " fue eliminada exitosamente");
    }

    /**
     * Método encargado de eliminar a un competidor de una carrera,
     * involucra 2 APIS, API "carrera" y API "competidor"
     * @param idCarrera
     * @param idCompetidor
     * @return
     */
    @Override
    public CarreraResponse eliminarCompetidor(Long idCarrera, Long idCompetidor){
        //Primero extraemos la carrera y verificamos que exista
        CarreraDTO carrera = repository.findById(idCarrera)
                .orElseThrow(() -> new RuntimeException("Esa carrera no existe"));

        //Verificamos que el competidor este inscrito en la carrera
        if(!carrera.getCompetidores().contains(idCompetidor)){
            throw new RuntimeException("El competidor no esta inscrito en esta carrera");
        }

        carrera.getCompetidores().remove(idCompetidor); //Se quita el id del competidor de la lista
        CarreraDTO guardada = repository.save(carrera);

        //Finalmente WebClient para avisar al proyecto "Competidor" que quite la carrera de su atributo
        webClientCompetidor.patch()
                .uri("/{idcompetidor}/quitarcarrera", idCompetidor)
                .retrieve()
                .bodyToMono(CompetidorResponse.class)
                .block();

        return modelMapperBean.map(guardada, CarreraResponse.class); //Se mapea de la entidad al Response
    }

    /**
     * Método que recibe la llamada del proyecto "Competidor" para agregar un competidor a la lista de la carrera
     * @param idCarrera
     * @param idCompetidor
     * @return
     */
    @Override
    public CarreraResponse agregarCompetidor(Long idCarrera, Long idCompetidor){
        //Extraemos la carrera y verificamos que exista
        CarreraDTO carrera = repository.findById(idCarrera)
                .orElseThrow(() -> new RuntimeException("Esa carrera no existe"));

        //Si el competidor no esta ya en la lista, se agrega
        if(!carrera.getCompetidores().contains(idCompetidor)){
            carrera.getCompetidores().add(idCompetidor);
        }
        CarreraDTO guardada = repository.save(carrera);

        return modelMapperBean.map(guardada, CarreraResponse.class);
    }

}
