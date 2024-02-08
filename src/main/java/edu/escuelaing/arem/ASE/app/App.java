package edu.escuelaing.arem.ASE.app;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Documentación de la clase App
 * Autor: Sergio López
 *
 * La clase App es la clase principal que inicia el servidor HTTP para el servicio de películas.
 * Utiliza un objeto HttpServer para manejar las solicitudes entrantes y procesarlas utilizando
 * la funcionalidad proporcionada por la clase HttpMovie.
 */
public class App {
    /**
     * Método principal que inicia el servidor HTTP y el servicio de películas.
     * @param args Argumentos de la línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        // Crea una instancia de HttpServer.
        HttpServer.start();
    }
}
