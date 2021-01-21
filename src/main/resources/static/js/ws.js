var stompClient = null;
var remindLaterURL = null;
function connect(){
    var socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame){
        stompClient.subscribe('/user/queue/message',function(message){
            showMessage(JSON.parse(message.body));
        });
    });
}
function disconnect(){
    if(stompClient!=null){
        stompClient.disconnect();
    }
}
function showMessage(message){
    var wsMessage = document.getElementById("wsMessage");
    document.getElementById("wsMessageTitle").innerText = message.title;
    document.getElementById("goToNote").innerText = message.content;
    document.getElementById("eventDate").innerText = message.date;
    document.getElementById("eventTime").innerText = message.time;
    document.getElementById("wsMessageForm").action = "/messages/setEdited/" + message.reminderId;
    remindLaterURL = "/messages/remindLater/" + message.reminderId;
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
function remindLater(){
    var token = $("meta[name='_csrf']").attr("content");
    $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
        jqXHR.setRequestHeader('X-CSRF-Token', token);
    });
    $.post(remindLaterURL);
    document.getElementById("wsMessage").style.bottom = "-160px";
    document.getElementById("wsMessage").style.display = "none";
}
function goToNote(){
    document.getElementById("wsMessageForm").submit();
}
