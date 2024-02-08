package edu.escuelaing.arem.ASE.app;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.JsonObject;

/**
 * La clase Cache implementa un mecanismo de almacenamiento en caché para objetos JsonObject
 * asociados con claves de tipo String. Utiliza un ConcurrentHashMap para garantizar la
 * concurrencia segura en entornos multiproceso.
 */
public class Cache {
    private ConcurrentHashMap<String, JsonObject> movieCache;
    private static Cache cache = null;

    /**
     * Constructor privado que inicializa el ConcurrentHashMap para almacenar la caché.
     */
    private Cache(){
        movieCache = new ConcurrentHashMap<String, JsonObject>();
    }

    /**
     * Método estático que devuelve una instancia única de la clase Cache.
     *
     * @return la instancia única de Cache
     */
    public static Cache getInstance(){
        if(cache == null){
            cache = new Cache();
        }
        return cache;
    }

    /**
     * Obtiene el JsonObject asociado con la clave proporcionada desde la caché.
     *
     * @param name la clave asociada al JsonObject
     * @return el JsonObject asociado con la clave, o null si la clave no está presente en la caché
     */
    public JsonObject getMovie(String name){
        return movieCache.get(name);
    }

    /**
     * Verifica si la clave está presente en la caché.
     *
     * @param name la clave a verificar
     * @return true si la clave está presente en la caché, false de lo contrario
     */
    public boolean movieInCache(String name){
        return movieCache.containsKey(name);
    }

    /**
     * Agrega un nuevo JsonObject a la caché con la clave proporcionada si no existe ya una
     * entrada con la misma clave.
     *
     * @param name      la clave asociada al JsonObject
     * @param movieInfo el JsonObject a agregar a la caché
     */
    public void addMovieToCache(String name, JsonObject movieInfo){
        movieCache.putIfAbsent(name, movieInfo);
    }
}
