package com.edu.udistrital.backend.competidor.controller;

/**
 * Clase controller de tipo @RestController que contiene los endpoints y que delega la parte lógica al service
 */

import com.edu.udistrital.backend.competidor.service.ServiceCompetidor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
//Raíz en común de todos los endpoints
@RequestMapping("/api/competidor")
public class ControllerCompetidor {

    private ServiceCompetidor service;

    //Inyección por medio de constructor del service
    public ControllerCompetidor(ServiceCompetidor service){
        this.service = service;
    }

    @RequestMapping(value = "/crearcompetidor", method = RequestMethod.POST)
    public Response<entity> crearCompetidor (@RequestBody )


}
