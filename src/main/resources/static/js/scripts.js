
//Websocket
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
//Pokaż notkę
function goTo(){
    document.getElementById("wsMessageForm").submit();
    isDisplayed = false;
}

//Theme
//Zmień motyw
function submitThemeForm() {
    document.getElementById("themeForm").submit();
}

//Lista notek
//Wybierz kalendarz
function submitCalendarForm() {
    document.getElementById("calendarForm").submit();
}