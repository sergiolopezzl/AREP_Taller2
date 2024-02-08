console.log("Script loaded!");

// Example JavaScript code
document.getElementById('searchForm').addEventListener('submit', function(event) {
    event.preventDefault();
    var movieName = document.getElementById('movieName').value;
    searchMovie(movieName);
});

function searchMovie(movieName) {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                var response = JSON.parse(xhr.responseText);
                displaySearchResults(response);
            } else {
                console.error('Error en la solicitud: ' + xhr.status);
            }
        }
    };
    xhr.open('GET', '/search?movieName=' + encodeURIComponent(movieName));
    xhr.send();
}

function displaySearchResults(results) {
    var searchResultsDiv = document.getElementById('searchResults');
    searchResultsDiv.innerHTML = ''; // Limpiar resultados anteriores
    // Mostrar los resultados de la búsqueda en el DOM
}
