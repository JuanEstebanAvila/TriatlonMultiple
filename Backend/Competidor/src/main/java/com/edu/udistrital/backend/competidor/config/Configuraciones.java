package com.edu.udistrital.backend.competidor.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Properties;

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

    //Datos provenientes del archivo "application.properties", extrae los datos del archivo de propiedades para no exponer información directamente en el código
    @Value("${api.correo.contrasena}")
    private String contrasena;

    @Value("${api.correo.usuario}")
    private String usuario;

    @Value("${api.correo.puerto}")
    private int puerto;

    @Value("${api.correo.provedor}")
    private String provedor;

    /**
     * Configuración del correo
     * Código creado por : "Un Programador Nace"
     * Obtenido en : "https://www.youtube.com/watch?v=JKmzV1MY_-M&list=PLr23_YfwEbPTWw4-3h5Wqs_PIzbtBSQMM&index=12"
     */
    @Bean
    JavaMailSender getJavaMailSender(){
        JavaMailSenderImpl mail = new JavaMailSenderImpl();

        mail.setHost(provedor);//Provedor que usaremos, en nuestro caso Gmail de Google
        mail.setPort(puerto); //Puerto estandar para el envío
        mail.setUsername(usuario);//Correo que envíar el correo
        mail.setPassword(contrasena);//Clave de aplicación

        Properties properties = mail.getJavaMailProperties();
        properties.put("mail.trasport.protocol", "smtp"); //Establece el prótocolo para el envío del correo
        properties.put("mail.smtp.auth", "true"); //Cuando use el prótocolo se autenticará con el usuario y contraseña
        properties.put("mail.smtp.starttls.enable", "true"); //Toda la comunicación estará cifrada, como medida de seguridad
        properties.put("mail.debug", "true"); //Para que aparezca en consola si se envío o no el correo

        return mail;
    }

}
