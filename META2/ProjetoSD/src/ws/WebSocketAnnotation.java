package ws;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint(value = "/ws/{username}")
public class WebSocketAnnotation {
    private static final AtomicInteger sequence = new AtomicInteger(1);
    private String username;
    private Session session;
    public static final HashMap<String, Set<WebSocketAnnotation>> utilizadores =new HashMap<>();
    public WebSocketAnnotation() {
        //username = "User" + sequence.getAndIncrement();
    }

    @OnOpen
    public void start(@PathParam("username") String username, Session session) {
        this.session = session;

        //String message = "*" + username + "* connected.";
        //sendMessage(message);
    }

    @OnClose
    public void end() {
        utilizadores.get(this.username).remove(this);
        // clean up once the WebSocket connection is closed
    }

    @OnMessage
    public void receiveMessage(String message) {
        sendMessage("Entrei com o username "+message);
        this.username=message;
        if(!utilizadores.containsKey(this.username)){
            utilizadores.put(this.username,new CopyOnWriteArraySet<>());
        }
        utilizadores.get(this.username).add(this);
        // one should never trust the client, and sensitive HTML
        // characters should be replaced with &lt; &gt; &quot; &amp;
    }

    @OnError
    public void handleError(Throwable t) {
        t.printStackTrace();
    }

    public void sendMessage(String text) {
        // uses *this* object's session to call sendText()
        try {
            this.session.getBasicRemote().sendText(text);
        } catch (IOException e) {
            // clean up once the WebSocket connection is closed
            try {
                this.session.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}

