var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body));
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}

function showGreeting(message) {
	var str="<tr>";
	for(let i=0;i<message.length;i++){
		obj = message[i];
		l = parseInt(obj.chnageInOiPE) -  parseInt(obj.chnageInOiCE)
		str +="<td>"+obj.dateTime+"</td>";
		str +="<td>" + parseInt(obj.oiCE).toLocaleString('en-IN')+ "</td>";
		str +="<td>" + parseInt(obj.oiPE).toLocaleString('en-IN')+ "</td>";
		str +="<td>" + parseFloat(obj.strike).toLocaleString('en-IN')+ "</td>";
		str +="<td>" + parseInt(obj.chnageInOiCE).toLocaleString('en-IN')+ "</td>";
		str +="<td>" + l.toLocaleString('en-IN') + "</td>";
		str +="<td>" + parseInt(obj.chnageInOiPE).toLocaleString('en-IN')+ "</td></tr>";
	}
	$("#greetings").empty();
	$("#greetings").append(str);
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});
window.onload = function() {
  connect();
};