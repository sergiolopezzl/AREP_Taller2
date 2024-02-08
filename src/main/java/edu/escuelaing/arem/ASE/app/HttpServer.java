package edu.escuelaing.arem.ASE.app;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * La clase HttpServer implementa un servidor HTTP simple que puede manejar solicitudes
 * de clientes y responder con archivos estáticos o detalles de películas.
 */
public class HttpServer {

    private static final int PORT = 35000;
    private static final String PUBLIC_DIRECTORY = "public";
    private static final HttpMovie movieService = new HttpMovie();

    /**
     * Inicia el servidor HTTP y escucha en el puerto especificado.
     */
    public static void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Listening on port " + PORT + "...");
            while (true) {
                handleClient(serverSocket);
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + PORT + ". " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Maneja la solicitud del cliente.
     *
     * @param serverSocket el socket del servidor
     */
    private static void handleClient(ServerSocket serverSocket) {
        try (Socket clientSocket = serverSocket.accept();
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            String uriStr = extractURI(in);
            String outputLine;

            if (uriStr.startsWith("/movies")) {
                outputLine = sendMovieResponse(uriStr.replace("/movies?name=", ""));
            } else {
                outputLine = sendFileResponse(clientSocket.getOutputStream(), uriStr);
            }

            out.println(outputLine);
        } catch (IOException e) {
            System.err.println("Error handling client request: " + e.getMessage());
        }
    }

    /**
     * Extrae la URI de la solicitud HTTP.
     *
     * @param in el lector de entrada
     * @return la URI de la solicitud
     * @throws IOException si hay un error de entrada/salida
     */
    private static String extractURI(BufferedReader in) throws IOException {
        String inputLine;
        String uriStr = "";
        boolean isFirstLine = true;
        while ((inputLine = in.readLine()) != null) {
            if (isFirstLine) {
                uriStr = inputLine.split(" ")[1];
                isFirstLine = false;
            }
            if (!in.ready()) {
                break;
            }
        }
        return uriStr;
    }

    /**
     * Envía la respuesta del archivo al cliente.
     *
     * @param out    el flujo de salida
     * @param uriStr la URI de la solicitud
     * @return la respuesta HTTP
     * @throws IOException si hay un error de entrada/salida
     */
    private static String sendFileResponse(OutputStream out, String uriStr) throws IOException {
        String format = getContentType(uriStr);
        String body = sendFile(uriStr);
        String header = "HTTP/1.1 200 OK\r\n" + "Content-Type:" + format + "\r\n" + "\r\n";
        if (getContentType(uriStr).startsWith("image")) {
            try {
                out.write(header.getBytes());
                out.write(Image(uriStr));
            } catch (IOException ex) {
                Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return header + body;
    }

    /**
     * Lee el archivo y envía su contenido al cliente.
     *
     * @param filePath la ruta del archivo
     * @return el contenido del archivo como una cadena
     * @throws IOException si hay un error de entrada/salida
     */
    private static String sendFile(String filePath) throws IOException {
        Path file = (filePath.equals("/")) ? Paths.get(PUBLIC_DIRECTORY + "/search.html") : Paths.get(PUBLIC_DIRECTORY + filePath);
        StringBuilder outputLine = new StringBuilder();
        Charset charset = Charset.forName("UTF-8");
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            String line;
            while ((line = reader.readLine()) != null) {
                outputLine.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.format("IOException: " + e.getMessage(), e);
        }
        return outputLine.toString();
    }

    /**
     * Envía la respuesta de la película al cliente.
     *
     * @param uriStr el título de la película
     * @return la respuesta HTTP
     * @throws IOException si hay un error de entrada/salida
     */
    private static String sendMovieResponse(String uriStr) throws IOException {
        try {
            JsonObject resp = movieService.get(uriStr);
            JsonElement poster = resp.get("Poster");
            JsonElement title = resp.get("Title");
            JsonElement released = resp.get("Released");
            JsonElement genre = resp.get("Genre");
            JsonElement director = resp.get("Director");
            JsonElement actors = resp.get("Actors");
            JsonElement language = resp.get("Language");
            JsonElement plot = resp.get("Plot");
            String bodyFile = sendFile("/movie_details.html")
                    .replace("{Poster}", poster.getAsString())
                    .replace("{Title}", title.getAsString())
                    .replace("{Released}", released.getAsString())
                    .replace("{Genre}", genre.getAsString())
                    .replace("{Director}", director.getAsString())
                    .replace("{Actors}", actors.getAsString())
                    .replace("{Language}", language.getAsString())
                    .replace("{Plot}", plot.getAsString());

            return "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n" + "\r\n" + bodyFile;
        } catch (Exception e) {
            System.err.println("Error al procesar la película:");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Extrae el título de la película de la URI.
     *
     * @param uriStr la URI de la solicitud
     * @return el título de la película
     */
    private static String extractMovieTitle(String uriStr) {
        return uriStr.split("=")[1];
    }

    /**
     * Lee y devuelve la imagen como un arreglo de bytes.
     *
     * @param pathFile la ruta del archivo de la imagen
     * @return los bytes de la imagen
     */
    public static byte[] Image(String pathFile) {
        Path file = Paths.get("public" + pathFile);
        byte[] imageData = null;
        try {
            imageData = Files.readAllBytes(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageData;
    }

    /**
     * Obtiene el tipo de contenido del archivo según su extensión.
     *
     * @param filePath la ruta del archivo
     * @return el tipo de contenido del archivo
     */
    private static String getContentType(String filePath) {
        if (filePath.endsWith(".html") || filePath.endsWith("/")) {
            return "text/html";
        } else if (filePath.endsWith(".css")) {
            return "text/css";
        } else if (filePath.endsWith(".js")) {
            return "application/javascript";
        } else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
            return "image/jpg";
        } else if (filePath.endsWith(".png")) {
            return "image/png";
        } else if (filePath.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "text/plain";
        }
    }
}
