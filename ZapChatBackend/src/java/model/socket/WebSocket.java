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

                ServletOperations so = new ServletOperations();
                JsonObject jsonObject = so.LoadLastStatus(object);

                response_DTO = new Response_DTO(jsonObject.get("success").getAsBoolean(), so);
                session.getBasicRemote().sendText(gson.toJson(response_DTO));

                //others
                org.hibernate.Session hs = HibernateUtil.getSessionFactory().openSession();

                Criteria userCriteria = hs.createCriteria(User.class);
                List<User> userCriteriaList = userCriteria.list();

                for (User user : userCriteriaList) {

                    int id = user.getId();
                    if (clients.containsKey(id)) {

                        response_DTO = new Response_DTO(jsonObject.get("success").getAsBoolean(), so);
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

                String otherUserId1 = object.get("otherUserId").getAsString();
                JsonObject loadHome = operations.loadHome(object);

                if (loadHome.get("isFound").getAsBoolean()) {

                    response_DTO = new Response_DTO(true, loadHome);
                } else {

                    response_DTO = new Response_DTO(false, loadHome);
                }

                session.getBasicRemote().sendText(gson.toJson(response_DTO));

                if (clients.containsKey(otherUserId1)) {

                    object.remove("userId");
                    object.addProperty("userId", otherUserId1);
                    JsonObject loadHome2 = operations.loadHome(object);

                    JsonObject jo = new JsonObject();
                    jo.addProperty("success", loadHome.get("isFound").getAsBoolean());
                    jo.add("data", loadHome2);

                    System.out.println("jo " + jo);
                    clients.get(otherUserId1).getBasicRemote().sendText(gson.toJson(jo));
                }

                break;
            case "send_chat":

                //single chat
                String otherUserId = object.get("otherUserId").getAsString();

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
                    otherUserSaveChat.addProperty("side", "left");

                    JsonObject jo = new JsonObject();
                    jo.addProperty("success", otherUserSaveChat.get("isSuccess").getAsBoolean());
                    jo.addProperty("otherUserId", otherUserId);
                    jo.addProperty("fromUserId", object.get("fromUserId").getAsInt());
                    jo.add("data", otherUserSaveChat);

                    clients.get(otherUserId).getBasicRemote().sendText(gson.toJson(jo));
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
