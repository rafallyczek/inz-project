var stompClient = null;
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
    document.getElementById("wsMessageContent").innerHTML = "Nadchodzące wydarzenie: <span style='font-weight: bold'>"+message.content+"</span>";
    document.getElementById("wsMessageDate").innerHTML = "Data: <span style='text-decoration: underline'>"+message.date+"</span>";
    document.getElementById("wsMessageTime").innerHTML = "Godzina: <span style='text-decoration: underline'>"+message.time+"</span>";
    wsMessage.style.display = "block";
    var styles = window.getComputedStyle(wsMessage);
    var bottom = parseInt(styles.getPropertyValue("bottom"),10);
    if(bottom!==0){
        var interval = setInterval(move,5);
    }
    function move(){
        if(bottom!==0){
            bottom++;
            wsMessage.style.bottom = bottom+"px";
        }else{
            clearInterval(interval);
        }
    }
}