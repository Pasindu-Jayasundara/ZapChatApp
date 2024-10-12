package model.socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entity.Group_member;
import entity.Group_table;
import entity.User;
import javax.websocket.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.server.ServerEndpoint;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

@ServerEndpoint("/WebSocket")
public class WebSocket {

    private static Map<String, Session> clients = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("New WebSocket connection: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("WebSocket received message: " + message);

        Gson gson = new Gson();
        JsonObject object = gson.fromJson(message, JsonObject.class);
        String type = object.get("location").getAsString();

        SocketOperations operations = new SocketOperations();
        Response_DTO response_DTO = null;

        switch (type) {
            case "status":
                
                System.out.println("status: "+object);

                ServletOperations so = new ServletOperations();
                JsonObject jsonObject = so.LoadLastStatus(object);
                
                System.out.println("json object: "+jsonObject);

                response_DTO = new Response_DTO(jsonObject.get("success").getAsBoolean(), jsonObject);
                session.getBasicRemote().sendText(gson.toJson(response_DTO));

                //others
                org.hibernate.Session hs = HibernateUtil.getSessionFactory().openSession();

                Criteria userCriteria = hs.createCriteria(User.class);
                List<User> userCriteriaList = userCriteria.list();

                for (User user : userCriteriaList) {

                    int id = user.getId();
                    if (clients.containsKey(id)) {

                        response_DTO = new Response_DTO(jsonObject.get("success").getAsBoolean(), jsonObject);
                        clients.get(id).getBasicRemote().sendText(gson.toJson(response_DTO));
                    }

                }
                hs.close();

                break;
            case "send_group_chat":
                //location: "send_group_chat",
                //groupId: data.groupId,
                //contentType: "Message",
                //content: getText,
                //user:parsedUser

                JsonObject groupchat = operations.sendGroupChat(object);

                if (groupchat.get("isSuccess").getAsBoolean()) {
                    response_DTO = new Response_DTO(true, groupchat);
                } else {
                    response_DTO = new Response_DTO(false, groupchat);
                }

                session.getBasicRemote().sendText(gson.toJson(response_DTO));

                //load group memebers to send message
                org.hibernate.Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
                int groupId = object.get("groupId").getAsInt();
                Group_table group = (Group_table) hibernateSession.get(Group_table.class, groupId);

                JsonObject jsonuser = object.get("user").getAsJsonObject();
                User user = (User) hibernateSession.get(User.class, jsonuser.get("id").getAsInt());

                Criteria groupMemberCriteria = hibernateSession.createCriteria(Group_member.class);
                groupMemberCriteria.add(Restrictions.and(
                        Restrictions.ne("user", user),
                        Restrictions.eq("group_table", group)
                ));
                List<Group_member> groupMemberList = groupMemberCriteria.list();

                for (Group_member group_member : groupMemberList) {

                    int id = group_member.getUser().getId();
                    if (clients.containsKey(id)) {

                        JsonObject jo = new JsonObject();
                        jo.addProperty("success", groupchat.get("isSuccess").getAsBoolean());
                        jo.add("data", groupchat);

                        clients.get(id).getBasicRemote().sendText(gson.toJson(jo));
                    }

                }
                hibernateSession.close();

                break;

            case "home":

                org.hibernate.Session hibernateSessionHome = HibernateUtil.getSessionFactory().openSession();

                String otherUserId2 = object.get("userId").getAsString(); //from
                String otherUserId1 = object.get("otherUserId").getAsString(); //to

                JsonObject fromloadHome = operations.loadHome(object);
                JsonObject toloadHome = fromloadHome;

                //from
                JsonObject fromjo = fromloadHome.get("data").getAsJsonObject();
                fromloadHome.remove("data");

                User fromUser = (User) hibernateSessionHome.get(User.class, Integer.parseInt(otherUserId1));

                fromjo.addProperty("userId", otherUserId2);
                fromjo.addProperty("name", fromUser.getFirst_name() + " " + fromUser.getLast_name());
                fromjo.addProperty("Image", fromUser.getProfile_image());
                fromjo.addProperty("onlineStatus", fromUser.getUser_online_status().getStatus());
                fromjo.addProperty("about", fromUser.getAbout());
                fromjo.addProperty("showTick", Boolean.FALSE);

                fromloadHome.add("data", new Gson().toJsonTree(fromjo));

                response_DTO = new Response_DTO(fromloadHome.get("isFound").getAsBoolean(), fromloadHome);
                session.getBasicRemote().sendText(gson.toJson(response_DTO));

                //to
                if (clients.containsKey(otherUserId1)) {

                    JsonObject tojo = toloadHome.get("data").getAsJsonObject();
                    toloadHome.remove("data");

                    User toUser = (User) hibernateSessionHome.get(User.class, Integer.parseInt(otherUserId2));

                    tojo.addProperty("userId", Integer.parseInt(otherUserId2));
                    tojo.addProperty("name", toUser.getFirst_name() + " " + toUser.getLast_name());
                    tojo.addProperty("Image", toUser.getProfile_image());
                    tojo.addProperty("onlineStatus", toUser.getUser_online_status().getStatus());
                    tojo.addProperty("about", toUser.getAbout());
                    tojo.addProperty("showTick", Boolean.FALSE);

                    toloadHome.add("data", new Gson().toJsonTree(tojo));

                    response_DTO = new Response_DTO(toloadHome.get("isFound").getAsBoolean(), toloadHome);
                    clients.get(otherUserId1).getBasicRemote().sendText(gson.toJson(response_DTO));
                }

                break;
            case "send_chat":

                //single chat
                String otherUserId = object.get("otherUserId").getAsString();
                String fromUserId = object.get("fromUserId").getAsString();

                JsonObject saveChat = operations.saveChat(object);
                JsonObject otherUserSaveChat = saveChat;

                if (saveChat.get("isSuccess").getAsBoolean()) {
                    saveChat.addProperty("side", "right");
                    saveChat.addProperty("otherUserId", otherUserId);
                    saveChat.addProperty("fromUserId", object.get("fromUserId").getAsInt());
                    response_DTO = new Response_DTO(true, saveChat);
                } else {
                    response_DTO = new Response_DTO(false, saveChat);
                }

                session.getBasicRemote().sendText(gson.toJson(response_DTO));

                if (clients.containsKey(otherUserId)) {

                    if (otherUserSaveChat.get("isSuccess").getAsBoolean()) {
                        otherUserSaveChat.addProperty("side", "left");
                        otherUserSaveChat.addProperty("otherUserId", otherUserId);
                        saveChat.addProperty("fromUserId", fromUserId);
                        response_DTO = new Response_DTO(true, otherUserSaveChat);
                    } else {
                        response_DTO = new Response_DTO(false, otherUserSaveChat);
                    }

                    clients.get(otherUserId).getBasicRemote().sendText(gson.toJson(response_DTO));
                }

                break;

            case "login":

                JsonObject login = operations.login(object);

                if (login.get("success").getAsBoolean()) {

                    String id = login.get("user").getAsJsonObject().get("id").getAsString();
                    if (!clients.containsKey(id)) {
                        clients.put(id, session);
                    }
                    response_DTO = new Response_DTO(true, login);

                } else {
                    response_DTO = new Response_DTO(false, login);
                }

                session.getBasicRemote().sendText(gson.toJson(response_DTO));

                break;
            default:
                break;
        }
    }


    @OnClose
    public void onClose(Session session) {
        System.out.println("WebSocket connection closed: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("WebSocket error occurred: " + throwable.getMessage());
    }

}
