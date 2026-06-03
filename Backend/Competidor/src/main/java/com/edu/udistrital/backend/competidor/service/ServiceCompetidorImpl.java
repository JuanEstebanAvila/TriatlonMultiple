package com.edu.udistrital.backend.competidor.service;

import com.edu.udistrital.backend.competidor.interfaces.Interfaces;
import com.edu.udistrital.backend.competidor.model.CarreraResponse;
import com.edu.udistrital.backend.competidor.model.CompetidorDTO;
import com.edu.udistrital.backend.competidor.model.CompetidorResponse;
import com.edu.udistrital.backend.competidor.repository.CompetidorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase Service, encargada de implementar toda la lógica de negocio y por lo tanto de los endpoints
 */
@Service
public class ServiceCompetidorImpl implements Interfaces{

    //Inyección por constructor de las dependencias
    private final JavaMailSender mail;
    private final WebClient webClientBean;
    private final CompetidorRepository repository;
    private final ModelMapper modelMapperBean;

    public ServiceCompetidorImpl(JavaMailSender mail, ModelMapper modelMapperBean, CompetidorRepository repository, WebClient webClientBean){
        this.mail = mail;
        this.modelMapperBean = modelMapperBean;
        this.webClientBean = webClientBean;
        this.repository = repository;
    }


    //Declaración a nivel las variables con la información necesaria para envíar el correo
    @Value("${api.correo.usuario}")
    private String remitente;
    @Value("${api.correo.asunto}")
    private String subject;
    @Value("${api.correo.mensaje}")
    private String texto;

    //Delega al Repository para que guarde un nuevo competidor con los datos que llegan del Front y envíarle un correo de verificación

    /**
     * Delega al Repository para que guarde un nuevo competidor
     * con los datos que llegan del Front y envíarle un correo de verificación
     *
     * @param datosCompetidor
     * @return
     */
    @Override
    public CompetidorResponse crearCompetidor(CompetidorDTO datosCompetidor){
        //Primero se valida que no haya un competidor con la misma identificación ya guardadó
        if(repository.findByIdentificacion(datosCompetidor.getIdentificacion()).isPresent()){
            //Y se le dice al cliente que ya existe un competidor con esa identificación
            throw new RuntimeException("Ya existe un competidor con la identificación " +datosCompetidor.getIdentificacion());
        }

        CompetidorDTO creado = repository.save(datosCompetidor);

        //Envío del correo
        enviarCorreo(datosCompetidor.getCorreo(), subject, texto);
        return modelMapperBean.map(creado, CompetidorResponse.class);
    }


    @Override
    public void enviarCorreo(String destinatario, String asunto, String mensaje){
        //Declaración a nivel de método de la información necesaria para envíar el correo

        SimpleMailMessage mailMessage = new SimpleMailMessage(); //Objeto que ayuda a construir el mensaje

        mailMessage.setFrom(remitente);
        mailMessage.setTo(destinatario);
        mailMessage.setSubject(asunto);
        mailMessage.setText(mensaje);

        mail.send(mailMessage);//Se envía el correo usando el objeto de tipo JavaMailSender
    }


    /**
     * Método para modificar el nombre de un competidor
     * @param id
     * @param nombreNuevo
     */
    @Override
    public CompetidorResponse modNombre(Long id, String nombreNuevo){
        CompetidorDTO competidor = repository.findById(id)
                .orElseThrow(()-> new RuntimeException("No existe un competidor con el id:" + id));

        //Si todo sale cambia el nombre y guarda el cambio
        competidor.setNombre(nombreNuevo);
        CompetidorDTO guardado = repository.save(competidor);

        return modelMapperBean.map(guardado, CompetidorResponse.class);
    }


    /**
     * Método de modifica la identificación (cédula) de un competidor
     * @param id
     * @param nuevaIdentificacion
     * @return
     */
    @Override
    public CompetidorResponse modIdentificacion(Long id, int nuevaIdentificacion){
        CompetidorDTO competidor = repository.findById(id)
                .orElseThrow(()->new RuntimeException("No existe un competidor con id " +id));

        competidor.setIdentificacion(nuevaIdentificacion);
        CompetidorDTO guardado = repository.save(competidor);

        return modelMapperBean.map(guardado, CompetidorResponse.class);
    }


    /**
     * Método que modifica la categoría de un competidor, delegando a repository
     * @param id
     * @param nuevaCategoria
     * @return
     */
    @Override
    public CompetidorResponse modCategoriaEdad(Long id, String nuevaCategoria){
        CompetidorDTO competidor = repository.findById(id)
                .orElseThrow(()->new RuntimeException("No existe un competidor con id " +id));

        competidor.setCategoria(nuevaCategoria);
        CompetidorDTO guardado = repository.save(competidor);

        return modelMapperBean.map(guardado, CompetidorResponse.class);
    }


    /**
     * Método de modifica el género de un competidor, primero buscándolo, corroborando que no exista y finalmente modificando
     * el atributo y guardándolo
     * @param id
     * @param nuevoGenero
     * @return
     */
    @Override
    public CompetidorResponse modGenero(Long id, String nuevoGenero){
        CompetidorDTO competidor = repository.findById(id)
                .orElseThrow(()->new RuntimeException("No existe un competidor con id " +id));

        competidor.setGenero(nuevoGenero);
        CompetidorDTO guardado = repository.save(competidor);

        return modelMapperBean.map(guardado, CompetidorResponse.class);
    }

    /**
     * Método para modificar las preferencias de un competidor
     * @param id
     * @param nuevaEspecialidad
     * @param nuevaModalidadCross
     * @return
     */
    @Override
    public CompetidorResponse modificarPreferencias(Long id, String nuevaEspecialidad, Boolean nuevaModalidadCross){
        CompetidorDTO competidor = repository.findById(id) //Extraemos el competidor y verificamos que exista
                .orElseThrow(()-> new RuntimeException("No existe el competidor con id : " + id));

        competidor.setEspecialidad(nuevaEspecialidad); //Actualiza la especialidad
        competidor.setModalidadCross(nuevaModalidadCross);//Actualiza la modalidad cross

        CompetidorDTO guardado = repository.save(competidor); //Guarda el competidor actualizado

        return modelMapperBean.map(guardado, CompetidorResponse.class);//Mapea de la entidad al response
    }

    /**
     * Método que bucsa a un competidor por su id y lo retorna como un objeto de tipo CompetidorResponse
     * @param id
     * @return
     */
    @Override
    public CompetidorResponse buscarCompetidor(Long id){
        CompetidorDTO competidor = repository.findById(id) //Primero se busca en la base de datos
                .orElseThrow(() -> new RuntimeException("No existe un competidor con id " + id )); //Corroboramos que exista

        return modelMapperBean.map(competidor, CompetidorResponse.class); //Si todo esta bien, se mapea a un objeto de tipo CompetidorResponse
    }

    /**
     * Método que consulta la lista de competidores según su género
     * @param genero
     * @return
     */
    @Override
    public List<CompetidorResponse> buscarGenero(String genero){
        List<CompetidorDTO> listaGenero = repository.findByGenero(genero);//Delegamos al service para que traiga la lista de atletas por género
        if(listaGenero.isEmpty()) {
            throw new RuntimeException("No existen competidores con ese género");
        }
        List<CompetidorResponse> listaGeneroResponse = new ArrayList<>(); //Necesitamos mapear a CompetidorResponse
        for(CompetidorDTO competidor : listaGenero){ //Usamos un for para recorrer todos los elementos del arreglo con elementos tipo CompetidorDTO
            listaGeneroResponse.add(modelMapperBean.map(competidor, CompetidorResponse.class));
        }
        return listaGeneroResponse;

    }


    /**
     * Método que busca la lista de competidores según su especialidad
     * @param especialidad
     * @return
     */
    @Override
    public List<CompetidorResponse> buscarEspecialidad(String especialidad){
        List<CompetidorDTO> listaEspecialidad = repository.findByEspecialidad(especialidad);//Delegamos al service para que traiga la lista de atletas por género
        if(listaEspecialidad.isEmpty()) {
                throw new RuntimeException("No existen competidores en esa especialidad");
        }
        List<CompetidorResponse> listaEspecialidadResponse = new ArrayList<>(); //Necesitamos mapear a CompetidorResponse
        for(CompetidorDTO competidor : listaEspecialidad){ //Usamos un for para recorrer todos los elementos del arreglo con elementos tipo CompetidorDTO
            listaEspecialidadResponse.add(modelMapperBean.map(competidor, CompetidorResponse.class));
        }
        return listaEspecialidadResponse;

    }

    /**
     * Método que busca la lista de competidores por categoria
     * @param categoria
     * @return
     */
    @Override
    public List<CompetidorResponse> buscarListaCategoria(String categoria){
        List<CompetidorDTO> listaCategoria = repository.findByCategoria(categoria);//Delegamos al service para que traiga la lista de atletas por género
        if(listaCategoria.isEmpty()) {
                throw new RuntimeException("No existen competidores en esa categoría");
        }
        List<CompetidorResponse> listaCategoriaResponse = new ArrayList<>(); //Necesitamos mapear a CompetidorResponse
        for(CompetidorDTO competidor : listaCategoria){ //Usamos un for para recorrer todos los elementos del arreglo con elementos tipo CompetidorDTO
            listaCategoriaResponse.add(modelMapperBean.map(competidor, CompetidorResponse.class));
        }
        return listaCategoriaResponse;

    }

    /**
     * Método para consultar la lista de competidores que esten en la modalidad cross
     * @param cross
     * @return
     */
    @Override
    public List<CompetidorResponse> buscarCross(Boolean cross){
        List<CompetidorDTO> listaCross = repository.findByModalidadCross(cross);//Delegamos al service para que traiga la lista de atletas por género
        if(listaCross.isEmpty()) {
            throw new RuntimeException("No existen competidores en modalidad cross");
        }
        List<CompetidorResponse> listaCrossResponse = new ArrayList<>(); //Necesitamos mapear a CompetidorResponse
        for(CompetidorDTO competidor : listaCross){ //Usamos un for para recorrer todos los elementos del arreglo con elementos tipo CompetidorDTO
            listaCrossResponse.add(modelMapperBean.map(competidor, CompetidorResponse.class));
        }
        return listaCrossResponse;

    }

    /**
     * Método para eliminar a un competidor por su id
     * @param id
     */
    public String eliminarCompetidor(Long id){
        if(repository.findById(id).isEmpty()) { //Primer se verifica que exista el competidor
            throw new RuntimeException("No se puede eliminar un competidor que no existe");
        }
        repository.deleteById(id); //Después de corroborar se delega al repository para que lo elimine

        return ("El competidor con id " + id + " fue eliminado exitosamente");
    }


    /**
     * Método encargado de registrar una carrera a un competidor,
     * involucra 2 APIS, API "competidor" y API "carrera"
     * @param idCompetidor
     * @param idCarrera
     * @return
     */
    @Override
    public CompetidorResponse registrarCarrera(Long idCompetidor, Long idCarrera){
        //Primero extraemos el competidor (identificado con el id) al cual vamos a registrarle una carrera
        CompetidorDTO competidor = repository.findById(idCompetidor)
                .orElseThrow(() -> new RuntimeException("Ese competidor no existe")); //Verificación de que exista

        //Después valida que la carrera si exista
        webClientBean.get()
                        .uri("/carrera/{idcarrera}", idCarrera)
                        .retrieve()
                        .bodyToMono(CarreraResponse.class)
                        .block();

        competidor.setIdCarrera(idCarrera); //Le asigna la carrera que mando el usuario desde el endpoint
        CompetidorDTO guardado = repository.save(competidor);

        //Finalmente WebCLient para hacer la petición al proyecto "Carrera" para que guarde el id del competidor
        webClientBean.patch()//Objeto inyectado en la parte superior y definido en la clase de configuración
                .uri("/{idcarrera}/agregarcompetidor/{idcompetidor}", idCarrera, idCompetidor)
                .retrieve()
                .bodyToMono(CarreraResponse.class)
                .block();
        return modelMapperBean.map(guardado,CompetidorResponse.class); //Se mapea de la entidad al Response
    }


    /**
     * Método para consultar la carrera a la cual pertenece un competidor
     * @param id
     * @return
     */
    public CarreraResponse consultarCarrera(Long id){
        CompetidorDTO competidor = repository.findById(id) //Busca a al competidor en la BD
                .orElseThrow(()-> new RuntimeException("El competidor no existe"));//Si no existe lanza una excepción

        if(competidor.getIdCarrera()==null){ //Comprobamos que el competidor consultado tenga una carrera asignada en su atributo
            throw new RuntimeException("El competidor no esta registrado a ninguna carrera");
        }

        //Si cumple con lo anterior se hace una peticióna al API "Carrera"
        CarreraResponse carrera = webClientBean.get()//De tipo consulta
                .uri("/carrera/{idcarrera}", competidor.getIdCarrera())//El endpoint del api "Carrera"
                .retrieve()//Envía la petición
                .bodyToMono(CarreraResponse.class)//Esperamos que devuelva un objeto de tipo CarreraResponse
                .block(); //Vuelve Sincrono

        return carrera;
    }


}
