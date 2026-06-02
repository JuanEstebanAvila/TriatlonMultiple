package com.edu.udistrital.backend.competidor.service;

import com.edu.udistrital.backend.competidor.interfaces.Interfaces;
import com.edu.udistrital.backend.competidor.model.CompetidorDTO;
import com.edu.udistrital.backend.competidor.model.CompetidorResponse;
import com.edu.udistrital.backend.competidor.repository.CompetidorRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase Service, encargada de implementar toda la lógica de negocio y por lo tanto de los endpoints
 */
public class ServiceCompetidorImpl implements Interfaces{

    //Inyección por constructor de las dependencias
    private final JavaMailSender mail;
    private final WebClient webClient;
    private final CompetidorRepository repository;
    private final ModelMapper modelMapper;

    public ServiceCompetidorImpl(JavaMailSender mail, ModelMapper modelMapper, CompetidorRepository repository, WebClient webClient){
        this.mail = mail;
        this.modelMapper = modelMapper;
        this.webClient = webClient;
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
        enviarCorreo(datosCompetidor.getCorreo(), subject, texto)
                .orElseThrow(()-> new RuntimeException("No se pudo envíar el correo"));

        return modelMapper.map(creado, CompetidorResponse.class);
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

        return modelMapper.map(competidor, CompetidorResponse.class);
    }


    /**
     * Método de modifica la identificación (cédula) de un competidor
     * @param id
     * @param nuevaIdentificacion
     * @return
     */
    @Override
    public CompetidorResponse modIdentificacion(Long id, String nuevaIdentificacion){
        CompetidorDTO competidor = repository.findById(id)
                .orElseThrow(()->new RuntimeException("No existe un competidor con id " +id));

        competidor.setIdentificacion(nuevaIdentificacion);
        CompetidorDTO guardado = repository.save(competidor);

        return modelMapper.map(guardado, CompetidorResponse.class);
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

        return modelMapper.map(guardado, CompetidorResponse.class);
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

        return modelMapper.map(guardado, CompetidorResponse.class);
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

        return modelMapper.map(competidor, CompetidorResponse.class); //Si todo esta bien, se mapea a un objeto de tipo CompetidorResponse
    }

    /**
     * Método que consulta la lista de competidores según su género
     * @param genero
     * @return
     */
    @Override
    public List<CompetidorResponse> buscarGenero(String genero){
        List<CompetidorDTO> listaGenero = repository.findByGenero(genero); //Delegamos al service para que traiga la lista de atletas por género

        List<CompetidorResponse> listaGeneroResponse = new ArrayList<>(); //Necesitamos mapear a CompetidorResponse
        for(CompetidorDTO competidor : listaGenero){ //Usamos un for para recorrer todos los elementos del arreglo con elementos tipo CompetidorDTO
            listaGeneroResponse.add(modelMapper.map(competidor, CompetidorResponse.class));
        }
        return listaGeneroResponse;

    }

    /**
     * Método que consulta la lista de competidores según su edad
     * @param edad
     * @return
     */
    @Override
    public List<CompetidorResponse> buscarEdad(String edad){
        List<CompetidorDTO> listaEdad = repository.findByEdad((edad); //Delegamos al service para que traiga la lista de atletas por género

        List<CompetidorResponse> listaEdadResponse = new ArrayList<>(); //Necesitamos mapear a CompetidorResponse
        for(CompetidorDTO competidor : listaEdad){ //Usamos un for para recorrer todos los elementos del arreglo con elementos tipo CompetidorDTO
            listaEdadResponse.add(modelMapper.map(competidor, CompetidorResponse.class));
        }
        return listaEdadResponse;

    }

    /**
     * Método que busca la lista de competidores según su especialidad
     * @param especialidad
     * @return
     */
    @Override
    public List<CompetidorResponse> buscarEspecialidad(String especialidad){
        List<CompetidorDTO> listaEspecialidad = repository.findByEspecialidad(especialidad); //Delegamos al service para que traiga la lista de atletas por género

        List<CompetidorResponse> listaEspecialidadResponse = new ArrayList<>(); //Necesitamos mapear a CompetidorResponse
        for(CompetidorDTO competidor : listaEspecialidad){ //Usamos un for para recorrer todos los elementos del arreglo con elementos tipo CompetidorDTO
            listaEspecialidadResponse.add(modelMapper.map(competidor, CompetidorResponse.class));
        }
        return listaEspecialidadResponse;

    }

    /**
     * Método para consultar la lista de competidores que esten en la modalidad cross
     * @param cross
     * @return
     */
    @Override
    public List<CompetidorResponse> buscarCross(Boolean cross){
        List<CompetidorDTO> listaCross = repository.findByModalidadCross((cross); //Delegamos al service para que traiga la lista de atletas por género

        List<CompetidorResponse> listaCrossResponse = new ArrayList<>(); //Necesitamos mapear a CompetidorResponse
        for(CompetidorDTO competidor : listaCross){ //Usamos un for para recorrer todos los elementos del arreglo con elementos tipo CompetidorDTO
            listaCrossResponse.add(modelMapper.map(competidor, CompetidorResponse.class));
        }
        return listaCrossResponse;

    }


}
