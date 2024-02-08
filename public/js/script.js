script = (() => {
    function loadGetMsg() {
        let nameMovie = document.getElementById("name").value;
        console.log(nameMovie);
        const xhttp = new XMLHttpRequest();
        xhttp.onload = function () {
            document.getElementById("getrespmsg").innerHTML =
                this.responseText;
        }
        xhttp.open("GET", "/movies?name=" + nameMovie);
        xhttp.send();
    }

    return{ 
        loadGetMsg
    } 
})();