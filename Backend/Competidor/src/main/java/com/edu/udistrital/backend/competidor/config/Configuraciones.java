package com.edu.udistrital.backend.competidor.config;

import com.edu.udistrital.backend.competidor.service.ServiceCompetidor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Clase de configuraciones del aplicativo, incorpora varios @Bean para las CORS, el ModelMapper y el WebClient
 */
@Configuration
public class Configuraciones {

    //Configuración global de las CORS, para que el Front pueda consumir los endpoints del proyecto
    @Bean
    public WebMvcConfigurer webMvcConfigurer(){
        return new WebMvcConfigurer() {
        @Override
        public void addCorsMappings (CorsRegistry registry){
            registry.addMapping("/**")//todos los EndPoints
                    .allowedMethods("*")//Todos los verbos
                    .allowedHeaders("*")//Acepta cualquier cabecera
                    .allowedOrigins("http://localhost:8383")//Url del Front
                    .maxAge(4000); //Tiempo que puede "recordar" el acceso, de 4 s
            }
        };
    }

    /**
     * Configuración del Modelmapper
     * @return
     */
    @Bean
    public ModelMapper modelMapperBean(){
        return new ModelMapper();
    }

    //Para cumplir con el Open/Close de la clase, la URL de la API "competidor", necesaria para algunos microservicios,
    //se extrae y guarda en una variable
    @Value("${api.url.carrera}")//Proviene del archivo "application.propierties" de la carpeta "resoruces"
    private String urlApiCarrera;

    /**
     * Configuración Bean de WebClient, para poder comunicarse con las otras APIS
     * @return
     */
    @Bean
    public WebClient webClientBean(){
        return WebClient.builder().baseUrl(urlApiCarrera).build(); //Se usa la variable que tiene el contenido en el properties
    }

}
