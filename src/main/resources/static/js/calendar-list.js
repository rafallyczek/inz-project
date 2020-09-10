//Wyświetl okno z danymi dnia
function modal(){
    var modal = document.getElementById("modal");

    modal.style.display = "block";

    window.onclick = function () {
        if(event.target == modal){
            modal.style.display = "none";
        }
    }
}