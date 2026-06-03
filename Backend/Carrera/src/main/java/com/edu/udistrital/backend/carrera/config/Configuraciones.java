package com.edu.udistrital.backend.carrera.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
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
                    .allowedOrigins("*")//Cualquier URL
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

    //Para cumplir con el Open/Close de la clase, la URL de la API "competidor"
    //se extrae y guarda en una variable
    @Value("${api.url.competidor}")//Proviene del archivo "application.properties" de la carpeta "resources"
    private String urlApiCompetidor;

    //La URL de la API "categoria"
    @Value("${api.url.categoria}")
    private String urlApiCategoria;

    /**
     * Configuración Bean de WebClient para comunicarse con la API "Competidor"
     * @return
     */
    @Bean
    @Qualifier("webClientCompetidor")
    public WebClient webClientCompetidor(){
        return WebClient.builder().baseUrl(urlApiCompetidor).build(); //Se usa la variable que tiene el contenido en el properties
    }

    /**
     * Configuración Bean de WebClient para comunicarse con la API "Categoria"
     * @return
     */
    @Bean
    @Qualifier("webClientCategoria")
    public WebClient webClientCategoria(){
        return WebClient.builder().baseUrl(urlApiCategoria).build(); //Se usa la variable que tiene el contenido en el properties
    }

}
