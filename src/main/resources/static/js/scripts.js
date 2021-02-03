//Websocket
//-----------------------------------------------------------------------------------------

//Zmienne
var stompClient = null;
var isDisplayed = false;
//Zasubskrybuj przypomnienia
function connect(){
    var socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame){
        stompClient.subscribe('/user/queue/message',function(message){
            if(!isDisplayed){
                showMessage(JSON.parse(message.body));
                isDisplayed = true;
            }
        });
    });
}
//Przerwij połączenie
function disconnect(){
    if(stompClient!=null){
        stompClient.disconnect();
    }
}
//Pokaż wiadomość
function showMessage(message){
    var wsMessage = document.getElementById("wsMessage");
    var data = message.data;
    document.getElementById("wsMessageTitle").innerText = message.title;
    document.getElementById("wsMessageContent").innerHTML = message.content;
    for(var key in data){
        if(data.hasOwnProperty(key)){
            if(key!=="id" && key!=="type"){
                var p = document.createElement("p");
                p.classList.add("wsMessageData");
                p.innerHTML = key+"<span class='bold'>"+data[key]+"</span>";
                wsMessage.appendChild(p);
            }else if(data[key]==="note"){
                var button = document.createElement("button");
                button.classList.add("remindLaterButton");
                button.setAttribute("onclick","remindLater("+data["id"]+")");
                button.innerText = "Przypomnij później";
                wsMessage.appendChild(button);
                document.getElementById("wsMessageForm").action = "/messages/goTo/note/"+data["id"];
            }else if(data[key]==="invitation"){
                document.getElementById("wsMessageForm").action = "/messages/goTo/messages/"+data["id"];
            }
        }
    }
    wsMessage.style.display = "block";
    var styles = window.getComputedStyle(wsMessage);
    var bottom = parseInt(styles.getPropertyValue("bottom"),10);
    if(bottom!==10){
        var interval = setInterval(move,5);
    }
    function move(){
        if(bottom!==10){
            bottom++;
            wsMessage.style.bottom = bottom+"px";
        }else{
            clearInterval(interval);
        }
    }
}
//Przypomnij później
function remindLater(id){
    var remindLaterURL = "/messages/remindLater/" + id;
    var token = $("meta[name='_csrf']").attr("content");
    $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
        jqXHR.setRequestHeader('X-CSRF-Token', token);
    });
    $.post(remindLaterURL);
    document.getElementById("wsMessage").style.bottom = "-160px";
    document.getElementById("wsMessage").style.display = "none";
    isDisplayed = false;
}
//Przejdź do zasobu
function goTo(){
    document.getElementById("wsMessageForm").submit();
    isDisplayed = false;
}

//Theme
//-----------------------------------------------------------------------------------------

//Zmień motyw
function submitThemeForm() {
    document.getElementById("themeForm").submit();
}

//Lista notek
//-----------------------------------------------------------------------------------------

//Wybierz kalendarz
function submitCalendarForm() {
    document.getElementById("calendarForm").submit();
}

//Dodawanie notek
//-----------------------------------------------------------------------------------------

//Pokaż lub schowaj pole z userId
function changeDisplay(){
    var div = document.getElementById("userIdInputs");
    if(div!==null){
        var styles = window.getComputedStyle(div);
        if(styles.getPropertyValue("display")==="none"){
            div.style.display = "block";
        }else{
            div.style.display = "none";
        }
    }
}

//Drag and Drop
//-----------------------------------------------------------------------------------------
var dragGridId = "";
//Zezwól na upuszczenie
function allow(event) {
    event.preventDefault();
}
//Podnieś
function drag(event) {
    event.dataTransfer.setData("text", event.target.id);
    var el = event.target;
    var className = el.className;
    while(className!=="taskGrid"){
        el = el.parentElement;
        className = el.className;
    }
    dragGridId = el.id;
}
//Upuść
function drop(event) {
    event.preventDefault();
    var el = event.target;
    var className = el.className;
    while(className!=="taskGrid"){
        el = el.parentElement;
        className = el.className;
    }
    var id = el.id;
    var data = event.dataTransfer.getData("text");
    var noteId = data.replace("note","");
    document.getElementById(id).insertBefore(document.getElementById(data),document.getElementById(id).lastElementChild);
    if(id==="grid1" && dragGridId!=="grid1"){
        statusToDo(noteId);
    }else if(id==="grid2" && dragGridId!=="grid2"){
        statusInProgress(noteId);
    }else if(id==="grid3" && dragGridId!=="grid3"){
        statusFinished(noteId);
    }
}
//Zmień status na do zrobienia
function statusToDo(noteId){
    setUpAJAX();
    $.ajax({
        url: "/notes/statusToDo/"+noteId,
        type: "POST",
        success: function(data){
            $(".taskGridWrapper").html(data);
        }
    });
}
//Zmień status na w trakcie
function statusInProgress(noteId){
    setUpAJAX();
    $.ajax({
        url: "/notes/statusInProgress/"+noteId,
        type: "POST",
        success: function(data){
            $(".taskGridWrapper").html(data);
        }
    });
}
//Zmień status na zakończone
function statusFinished(noteId){
    setUpAJAX();
    $.ajax({
        url: "/notes/statusFinished/"+noteId,
        type: "POST",
        success: function(data){
            $(".taskGridWrapper").html(data);
        }
    });
}
//AJAX - ogólne
//-----------------------------------------------------------------------------------------
function setUpAJAX(){
    var token = $("meta[name='_csrf']").attr("content");
    $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
        jqXHR.setRequestHeader('X-CSRF-Token', token);
    });
}