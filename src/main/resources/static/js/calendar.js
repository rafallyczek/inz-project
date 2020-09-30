//Wyświetl okno z danymi dnia
function modal(i){
    var modal = document.getElementById("modal"+i);

    modal.style.display = "block";

    window.onclick = function () {
        if(event.target == modal){
            modal.style.display = "none";
        }
    }
}
//Schowaj info i wyświetl dodawanie
function showAddForm(i,month,year){
    var info = document.getElementById("info"+i);
    var addForm = document.getElementById("addForm"+i)

    var stringMonth = month<10 ? "0"+month : month;
    var stringDay = i<10 ? "0"+i : i;

    document.getElementById("date"+i).value = year + "-" + stringMonth + "-" + stringDay;

    info.style.display = "none";
    addForm.style.display = "block";
}
//Schowaj dodawanie i wyświetl info
function showInfo(i){
    var info = document.getElementById("info"+i);
    var addForm = document.getElementById("addForm"+i)
    var editForm = document.getElementById("editForm"+i);

    editForm.style.display = "none";
    addForm.style.display = "none";
    info.style.display = "block";
}
//Wyświetl formularz edycji
function showEditForm(i,id,userId,title,content,date,time) {
    var info = document.getElementById("info"+i);
    var editForm = document.getElementById("editForm"+i);

    document.getElementById("idEdit"+i).value = id;
    document.getElementById("userIdEdit"+i).value = userId;
    document.getElementById("titleEdit"+i).value = title;
    document.getElementById("contentEdit"+i).value = content;
    document.getElementById("dateEdit"+i).value = date;
    document.getElementById("timeEdit"+i).value = time;

    document.getElementById("modalEditHeader"+i).innerHTML = "Edytuj notkę: "+title;

    info.style.display = "none";
    editForm.style.display = "block";
}