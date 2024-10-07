package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entity.Message;
import entity.Single_chat;
import entity.User;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadChat", urlPatterns = {"/LoadChat"})
public class LoadChat extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        boolean isSearch = (boolean) request.getAttribute("isSearch");
        String searchText = (String) request.getAttribute("searchText");
        User user = (User) request.getSession().getAttribute("user");

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        Criteria userCriteria = hibernateSession.createCriteria(User.class);
        userCriteria.add(Restrictions.ne("id", user.getId()));
        List<User> userList = userCriteria.list();

        ArrayList<Single_chat> chatArray = new ArrayList<>();
        ArrayList<Single_chat> searchChatArray = new ArrayList<>();

        boolean isFound = false;

        JsonArray jsonArray = new JsonArray();

        if (!userList.isEmpty()) {
            //has more users

            for (User otherUser : userList) {

                Criteria chatCriteria = hibernateSession.createCriteria(Single_chat.class);
                chatCriteria.add(Restrictions.or(
                        Restrictions.and(
                                Restrictions.eq("from_user", user),
                                Restrictions.eq("to_user", otherUser)
                        ),
                        Restrictions.and(
                                Restrictions.eq("from_user", otherUser),
                                Restrictions.eq("to_user", user)
                        )
                ));
                chatCriteria.addOrder(Order.desc("id"));
                chatCriteria.setMaxResults(1);
                Single_chat lastChat = (Single_chat) chatCriteria.uniqueResult();

                if (lastChat != null) {
                    //has last chat

                    chatArray.add(lastChat);
                }

            }

            if (isSearch) {

                String searchLower = searchText.toLowerCase();

                for (Single_chat single_chat : chatArray) {

                    String fromFirstName = single_chat.getFrom_user().getFirst_name().toLowerCase();
                    String fromLastName = single_chat.getFrom_user().getLast_name().toLowerCase();
                    String toFirstName = single_chat.getTo_user().getFirst_name().toLowerCase();
                    String toLastName = single_chat.getTo_user().getLast_name().toLowerCase();

                    if (fromFirstName.contains(searchLower)
                            || fromLastName.contains(searchLower)
                            || toFirstName.contains(searchLower)
                            || toLastName.contains(searchLower)) {

                        searchChatArray.add(single_chat);
                    }

                }
            }

            ArrayList<Single_chat> searchFrom;
            if (isSearch) {
                searchFrom = searchChatArray;
            } else {
                searchFrom = chatArray;
            }
            for (Single_chat single_chat : searchFrom) {

                if (!isFound) {
                    isFound = true;
                }

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("chatId", single_chat.getId());

                if (user.getId() == single_chat.getFrom_user().getId()) {
                    //i have send this message
                    //need to get to user details

                    jsonObject.addProperty("userId", single_chat.getTo_user().getId());
                    jsonObject.addProperty("name", single_chat.getTo_user().getFirst_name() + " " + single_chat.getTo_user().getLast_name());

                    if (single_chat.getTo_user().getProfile_image().equals("../assets/images/default.svg")) {
                        jsonObject.addProperty("image", "../assets/images/person-square.svg");

                    } else {
                        jsonObject.addProperty("image", single_chat.getTo_user().getProfile_image());
                    }

                    jsonObject.addProperty("onlineStatus", single_chat.getTo_user().getUser_online_status().getStatus());
                    jsonObject.addProperty("about", single_chat.getTo_user().getAbout());

                } else {
                    //received message
                    //need to get from user details

                    jsonObject.addProperty("userId", single_chat.getFrom_user().getId());
                    jsonObject.addProperty("name", single_chat.getFrom_user().getFirst_name() + " " + single_chat.getFrom_user().getLast_name());

                    if (single_chat.getFrom_user().getProfile_image().equals("../assets/images/default.svg")) {
                        jsonObject.addProperty("image", "../assets/images/person-square.svg");

                    } else {
                        jsonObject.addProperty("image", single_chat.getTo_user().getProfile_image());
                    }

                    jsonObject.addProperty("onlineStatus", single_chat.getFrom_user().getUser_online_status().getStatus());
                    jsonObject.addProperty("about", single_chat.getFrom_user().getAbout());

                }
                jsonObject.addProperty("messageStatus", single_chat.getMessage_status().getStatus());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                SimpleDateFormat time = new SimpleDateFormat("HH:mm");
                SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");

                boolean equal = date.format(new Date()).equals(date.format(single_chat.getDatetime()));
                if (equal) {
                    //same day
                    jsonObject.addProperty("datetime", time.format(single_chat.getDatetime()));

                } else {
                    jsonObject.addProperty("datetime", sdf.format(single_chat.getDatetime()));

                }

                if (single_chat.getMessage_content_type().getType().equals("Message")) {

                    Criteria msgCriteria = hibernateSession.createCriteria(Message.class);
                    msgCriteria.add(Restrictions.eq("single_chat", single_chat));

                    Message msg = (Message) msgCriteria.uniqueResult();

                    jsonObject.addProperty("lastMessage", msg.getMessage());

                } else {
                    jsonObject.addProperty("lastMessage", "File Content");

                }
                jsonArray.add(jsonObject);
            }

        }

        Gson gson = new Gson();

        JsonObject jo = new JsonObject();
        jo.addProperty("profile", user.getProfile_image());
        jo.addProperty("isFound", isFound);
        jo.add("data", gson.toJsonTree(jsonArray));

        Response_DTO response_DTO = new Response_DTO(true, gson.toJsonTree(jo));

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));

    }

}
