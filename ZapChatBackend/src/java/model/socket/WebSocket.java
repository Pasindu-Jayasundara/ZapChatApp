package model.socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.websocket.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/WebSocket")
public class WebSocket {

    private static final Set<Session> clients = new CopyOnWriteArraySet<>();
    
    private static final String SINGLE_CHAT = "single_chat_send";
    private static final String GROUP_CHAT = "group_chat_send";
    private static final String STATUS = "status_uploaded";
    private static final String HOME = "status_uploaded";
    private static final String PROFILE = "status_uploaded";

    @OnOpen
    public void onOpen(Session session) {
        clients.add(session);
        System.out.println("New WebSocket connection: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("WebSocket received message: " + message);
        
        Gson gson = new Gson();
        String action = gson.fromJson( message, JsonObject.class).get("action").getAsString();
        
        for (Session clientSession : clients) {
            
            switch (action) {
                case SINGLE_CHAT:
                    break;
                case GROUP_CHAT:
                    break;
                case STATUS:
                    break;
                default:
                    break;
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        clients.remove(session);
        System.out.println("WebSocket connection closed: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("WebSocket error occurred: " + throwable.getMessage());
    }
    
}
