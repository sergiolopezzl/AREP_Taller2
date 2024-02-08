package edu.escuelaing.arem.ASE.app;

import java.io.*;
import java.net.*;
import com.google.gson.JsonObject;

public class FileServer {
    private static final int PORT = 8080;
    private static final String ROOT_DIRECTORY = "public";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Listening on port " + PORT + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClientRequest(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
        }
    }

    private static void handleClientRequest(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream outStream = clientSocket.getOutputStream()) {

            String request = getRequest(in);
            String filePath = ROOT_DIRECTORY + request;

            if (request.startsWith("/search")) {
                sendResponse(outStream, "HTTP/1.1 200 OK", "text/html", loadHtmlContent("search.html"));
            } else {
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    sendResponse(outStream, "HTTP/1.1 200 OK", getContentType(filePath), readFileContent(file));
                } else {
                    // Handle 404 Not Found
                    sendResponse(outStream, "HTTP/1.1 404 Not Found", "text/plain", "404 Not Found".getBytes());
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client request: " + e.getMessage());
        }
    }

    private static String getRequest(BufferedReader in) throws IOException {
        String requestLine = in.readLine();
        if (requestLine != null) {
            String[] requestParts = requestLine.split("\\s+");
            return requestParts[1];
        }
        return "";
    }

    private static void sendResponse(OutputStream outStream, String status, String contentType, byte[] content) throws IOException {
        PrintWriter out = new PrintWriter(outStream);
        out.println(status);
        out.println("Content-Type: " + contentType);
        out.println();
        out.flush();
        outStream.write(content);
        outStream.flush();
    }

    private static String getContentType(String filePath) {
        if (filePath.endsWith(".html")) {
            return "text/html";
        } else if (filePath.endsWith(".css")) {
            return "text/css";
        } else if (filePath.endsWith(".js")) {
            return "application/javascript";
        } else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filePath.endsWith(".png")) {
            return "image/png";
        } else if (filePath.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "text/plain";
        }
    }

    private static byte[] readFileContent(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        }
    }

    private static byte[] loadHtmlContent(String fileName) throws IOException {
        String filePath = ROOT_DIRECTORY + File.separator + fileName;
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return readFileContent(file);
        } else {
            return "<html><body><h1>404 Not Found</h1></body></html>".getBytes();
        }
    }
}
