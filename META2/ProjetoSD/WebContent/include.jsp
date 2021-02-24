<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags"%>


<html>
<head>
    <title>UcBusca</title>
    <style>
        #container {
            position: absolute;
            right: 0%;
            width: 400px;
        }

        #history {
            height: 180px;
            border: 1px solid #AACAAC;
            padding: 5px;
            font-family: Courier, monospace;
            font-size: .9em;
            overflow-y: scroll;
            width: 100%;
        }

        #history p {
            margin: 0;
            padding: 0;
        }
    </style>
    <script type="text/javascript">
        var websocket = null;

        window.onload = function() { // URI = ws://10.16.0.165:8080/WebSocket/ws
            var user= document.getElementById("buscarNome").getAttribute('value');
            console.log(1)
            connect('wss://' + window.location.host + '/ProjetoSD/ws/' + user );
            console.log(2)
        }

        function connect(host) { // connect to the host websocket

            if ('WebSocket' in window)
                websocket = new WebSocket(host);
            else if ('MozWebSocket' in window)
                websocket = new MozWebSocket(host);
            else {
                writeToHistory('Get a real browser which supports WebSocket.');
                return;
            }
            console.log(3)

            websocket.onopen    = onOpen; // set the 4 event listeners below
            websocket.onclose   = onClose;
            websocket.onmessage = onMessage;
            websocket.onerror   = onError;
            console.log(4)
        }

        function onOpen(event) {
            var user=document.getElementById('buscarNome').getAttribute('value');
            writeToHistory('Notificacoes:');
            writeToHistory('Connected to ' + window.location.host + '.');
            doSend(user);
        }

        function onClose(event) {
            console.log(5)
            writeToHistory('WebSocket closed (code ' + event.code + ').');

        }

        function onMessage(message) { // print the received message
            writeToHistory(message.data);
        }

        function onError(event) {
            writeToHistory('WebSocket error.');

        }

        function doSend(message) {
            console.log(websocket)
                websocket.send(message); // send the message to the server
        }

        function writeToHistory(text) {
            var history = document.getElementById('history');
            var line = document.createElement('p');
            line.style.wordWrap = 'break-word';
            line.innerHTML = text;
            history.appendChild(line);
            history.scrollTop = history.scrollHeight;
        }

    </script>
</head>
<body>
<div id="buscarNome" value="${session.username}" style="display: none"></div>
    <div id="container">
        <div id="history">

        </div>
    </div>
</body>
</html>
