package edu.escuelaing.arem.ASE.app;

import com.google.gson.JsonObject;

import java.io.*;
import java.net.*;

public class HttpServer {
    private static final int PORT = 35000;
    private static final String PUBLIC_DIRECTORY = "public";
    private HttpMovie movieService;

    public HttpServer(HttpMovie movieService) {
        this.movieService = movieService;
    }

    public static void main(String[] args) {
        HttpMovie movieService = new HttpMovie();
        HttpServer server = new HttpServer(movieService);
        server.start();
    }

    public void start() {
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

    private void handleClient(ServerSocket serverSocket) throws IOException {
        try (Socket clientSocket = serverSocket.accept();
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            String uriStr = extractURI(in);
            if (uriStr.startsWith("/movie")) {
                sendMovieResponse(out, uriStr);
            } else {
                sendFileResponse(out, uriStr);
            }
        } catch (IOException e) {
            System.err.println("Error handling client request: " + e.getMessage());
        }
    }

    private String extractURI(BufferedReader in) throws IOException {
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

    private void sendFileResponse(PrintWriter out, String uriStr) throws IOException {
        String filePath = PUBLIC_DIRECTORY + uriStr;

        File file = new File(filePath);
        if (file.exists() && !file.isDirectory()) {
            sendFile(out, file);
        } else {
            sendNotFoundResponse(out);
        }
    }

    private void sendFile(PrintWriter out, File file) throws IOException {
        String contentType = getContentType(file.getName());

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: " + contentType);
            out.println();

            String line;
            while ((line = br.readLine()) != null) {
                out.println(line);
            }
        }
    }

    private void sendNotFoundResponse(PrintWriter out) {
        out.println("HTTP/1.1 404 Not Found");
        out.println("Content-Type: text/html");
        out.println();
        out.println("<html><body><h2>404 Not Found</h2></body></html>");
    }

    private String getContentType(String fileName) {
        if (fileName.endsWith(".html")) {
            return "text/html";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";

        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "text/plain";
        }
    }

    private void sendMovieResponse(PrintWriter out, String uriStr) throws IOException {
        String movieTitle = extractMovieTitle(uriStr);
        JsonObject response = movieService.get(uriStr);

        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: application/json");
        out.println();
        out.println(response.toString());
    }

    private String extractMovieTitle(String uriStr) {
        return uriStr.split("=")[1];
    }
}
