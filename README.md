### Sergio Daniel Lopez Vargas
# AREP_Taller2
Este Taller maneja solicitudes HTTP entrantes y proporcionar respuestas adecuadas tanto para archivos estáticos como para detalles de películas.. 
La aplicación proporciona una interfaz simple a través de un navegador 
web donde los usuarios pueden ingresar el título de la película y obtener 
detalles sobre la misma en formato JSON.

### Funcionamiento

La clase HttpServer implementa un servidor HTTP simple que puede manejar solicitudes de clientes y responder con archivos estáticos o detalles de películas.

El servidor escucha en un puerto específico (por defecto, el puerto 35000) y maneja las solicitudes entrantes de los clientes. Puede responder con archivos estáticos como HTML, CSS, JavaScript e imágenes, así como también puede proporcionar detalles de películas consultando un servicio externo.

La funcionalidad clave de la clase HttpServer se divide en varios métodos:

start(): Inicia el servidor HTTP y comienza a escuchar las solicitudes entrantes en un bucle infinito.
handleClient(ServerSocket serverSocket): Maneja la solicitud de un cliente aceptando la conexión del cliente, leyendo la URI de la solicitud y enviando la respuesta adecuada al cliente.
extractURI(BufferedReader in): Extrae la URI de la solicitud HTTP analizando la primera línea de la solicitud.
sendFileResponse(OutputStream out, String uriStr): Envía la respuesta del archivo al cliente, incluyendo el encabezado HTTP y el cuerpo del archivo.
sendFile(String filePath): Lee el contenido de un archivo y lo devuelve como una cadena de texto.
sendMovieResponse(String uriStr): Envía la respuesta de la película al cliente, buscando los detalles de la película utilizando la instancia de HttpMovie y reemplazando los marcadores de posición en una plantilla HTML.
extractMovieTitle(String uriStr): Extrae el título de la película de la URI de la solicitud.
Image(String pathFile): Lee y devuelve la imagen como un arreglo de bytes.
getContentType(String filePath): Obtiene el tipo de contenido del archivo según su extensión.

### Modular
El proyecto está organizado en módulos claros y distintos:
el servidor web (HttpServer) y la API para consultar películas
(HttpMovie).
Cada uno de estos módulos tiene su responsabilidad específica,
lo que facilita la comprensión y el mantenimiento del código.


### Instrucciones de Ejecución
* Clone el repositorio desde GitHub:

```git clone https://github.com/sergiolopezzl/AREP_Taller2.git```

* Navegue al directorio del proyecto: 

```cd AREP_Taller2```

* Compile el proyecto y descargue las dependencias con Maven: 

```mvn clean package```

* Ejecute el servidor utilizando el siguiente comando: 

```mvn exec:java '-Dexec.mainClass=edu.escuelaing.arem.ASE.app.App'```

Una vez que el servidor esté en funcionamiento, acceda a 
http://localhost:35000/ desde su navegador para comenzar a buscar películas.


Puede colocar http://localhost:35000/image.jpg  para ver la imagen del servidor y asi con cada archivo

### Diseño y Extensibilidad
El proyecto está diseñado de manera modular y extensible para permitir futuras expansiones y cambios en la funcionalidad del servidor. Algunas consideraciones sobre el diseño y la extensibilidad incluyen:

* Interfaz de Servicio:
  El proyecto sigue un enfoque basado en interfaces, lo que facilita la creación de implementaciones alternativas para diferentes partes del sistema. Por ejemplo, la interfaz HttpMovie define un contrato para interactuar con un servicio externo de detalles de películas, lo que permite la implementación de diferentes servicios de películas con APIs distintas sin modificar el código existente.

* Implementación de Proveedores de Servicios:
  La clase HttpMovie sirve como un proveedor de servicios para obtener detalles de películas de un servicio externo, como OMDB API en este caso. Esta implementación se puede cambiar fácilmente para utilizar otro servicio de detalles de películas o para agregar múltiples proveedores de servicios y alternar entre ellos según sea necesario.

* Configurabilidad:
  El proyecto se configura fácilmente mediante variables y parámetros definidos en el código. Por ejemplo, la configuración del puerto del servidor y la ubicación del directorio público se definen como constantes en la clase HttpServer, lo que facilita su ajuste según los requisitos del entorno de implementación.

* Manejo de Errores:
  El proyecto incluye mecanismos para manejar errores de manera adecuada y proporcionar retroalimentación útil en caso de fallos. Por ejemplo, se registran mensajes de error detallados en la consola en caso de que ocurran excepciones durante el manejo de solicitudes HTTP o la obtención de detalles de películas.