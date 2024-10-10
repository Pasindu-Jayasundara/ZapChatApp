//package controller;
package model.socket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/endpoint")
public class WSEndpoint {

//    private static Map<String, Session> clients = new ConcurrentHashMap<>();
//
//    @OnOpen
//    public void onOpen(Session session) {
//        System.out.println("New client connected with send chat: " + session.getId());
//    }
//
//    @OnMessage
//    public void onMessage(String message, Session session) throws IOException {
//        Response_DTO response_DTO = new Response_DTO();
//        System.out.println(message);
//
//        Gson gson = new Gson();
//        JsonObject object = gson.fromJson(message, JsonObject.class);
//        String type = object.get("type").getAsString();
//
//        SocketOperations operations = new SocketOperations();
//
//        if (type.equals("load")) {
//            String mobile = object.get("mobile").getAsString();
//
//            if (clients.containsKey(mobile)) {
//                clients.remove(mobile);
//            }
//
//            clients.put(mobile, session);
//
//            System.out.println("newly added number " + mobile);
//
//            response_DTO.setSuccess(true);
//            JsonObject responseObj = new JsonObject();
//            responseObj.addProperty("type", "chat_status");
//            responseObj.addProperty("status", 2);
//            responseObj.addProperty("who", mobile);
//
//            for (Map.Entry<String, Session> entry : clients.entrySet()) {
//                responseObj.addProperty("whose", entry.getKey());
//                response_DTO.setContent(responseObj);
//                entry.getValue().getBasicRemote().sendText(gson.toJson(response_DTO));
//            }
//            
//            operations.updateChatAsSeen(object);
//            
//            
//        } else if (type.equals("send_chat")) {
//            System.out.println("Clients currently connected: " + clients);
//
//            String receiver = object.get("receiver").getAsString();
//
//            JsonElement typeElement = object.get("mentionedId");
//
//            String mentionedId = ((typeElement == null) || (typeElement.isJsonNull())) ? null : object.get("mentionedId").getAsString();
//            object.addProperty("time", getTime());
//            object.addProperty("msgId", new Date().getTime());
//
//            response_DTO.setSuccess(true);
//
//            if (clients.containsKey(receiver)) {
//
//                object.addProperty("status", 2);
//                response_DTO.setContent(object);
//
//                clients.get(receiver).getBasicRemote().sendText(gson.toJson(response_DTO));
//                
//                System.out.println("send to both");
//            } else {
//                
//                object.addProperty("status", 1);
//                response_DTO.setContent(object);
//                
//                System.out.println("send to sender");
//            }
//
//            session.getBasicRemote().sendText(gson.toJson(response_DTO));
//            
//            System.out.println(gson.toJson(response_DTO));
//            operations.saveChat(object);
//
//        } else if (type.equals("chat_status")) {
//            String whose = object.get("whose").getAsString();
//
//            response_DTO.setSuccess(true);
//            response_DTO.setContent(object);
//            object.addProperty("status", 3);
//
//            session.getBasicRemote().sendText(gson.toJson(response_DTO));
//
//            if (clients.containsKey(whose)) {
//                clients.get(whose).getBasicRemote().sendText(gson.toJson(response_DTO));
//                System.out.println("send to both");
//            } else {
//                System.out.println("send to sender");
//            }
//
//            System.out.println(gson.toJson(response_DTO));
//            operations.updateChatAsSeen(object);
//
//        } else if (type.equals("newcomer")) {
//            response_DTO.setSuccess(true);
//            response_DTO.setContent(object);
//            for (Map.Entry<String, Session> entry : clients.entrySet()) {
//                entry.getValue().getBasicRemote().sendText(gson.toJson(response_DTO));
//            }
//        }
//    }
//
//    @OnClose
//    public void onClose(Session session) {
//        for (Map.Entry<String, Session> entry : clients.entrySet()) {
//            if (entry.getValue().equals(session)) {
//                clients.remove(entry.getKey());
//                System.out.println("Client disconnected: " + session.getId() + " " + entry.getKey());
//                break;
//            }
//        }
//    }
//
//    private String getTime() {
//        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm:ss a", Locale.ENGLISH));
//    }
}