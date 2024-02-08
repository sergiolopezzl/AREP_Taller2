package edu.escuelaing.arem.ASE.app;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 * Documentación de la clase HttpMovie
 *
 * La clase HttpMovie proporciona métodos para realizar solicitudes HTTP al servicio OMDB API
 * y recuperar información sobre películas utilizando el título de la película como parámetro de búsqueda.
 * Utiliza un objeto Cache para almacenar en caché las respuestas de las solicitudes HTTP para mejorar
 * el rendimiento y reducir la cantidad de solicitudes al servicio OMDB API.
 */
public class HttpMovie {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String API_KEY = "95abfe39";
    private static final String BASE_URL = "http://www.omdbapi.com/?apikey=" + API_KEY + "&t=";
    private final Cache cache;

    public HttpMovie() {
        this.cache = Cache.getInstance();
    }

    public JsonObject get(String URIStr) throws IOException {
        if (cache.movieInCache(URIStr)) {
            return cache.getMovie(URIStr);
        }

        URL obj = new URL(BASE_URL + URIStr);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
                cache.addMovieToCache(URIStr, jsonResponse);
                System.out.println(response.toString());
                return jsonResponse;
            }
        } else {
            System.out.println("No se realizó la petición");
        }
        return Cache.getInstance().getMovie(URIStr);
    }
}
