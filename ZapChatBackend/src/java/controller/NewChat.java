package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entity.Message;
import entity.Single_chat;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "NewChat", urlPatterns = {"/NewChat"})
public class NewChat extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String mobile = (String) request.getAttribute("mobile");
        User user = (User) request.getSession().getAttribute("user");

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        boolean isFound = false;
        boolean hasMessagedBefore = false;
        String message = "";

        //check if there is a user avaliable for this mobile number
        Criteria userCriteria = hibernateSession.createCriteria(User.class);
        userCriteria.add(Restrictions.eq("mobile", mobile));
        User searchedUser = (User) userCriteria.uniqueResult();

        JsonArray jsonArray = new JsonArray();

        if (searchedUser != null) {
            //search user avaliable

            isFound = true;

            Criteria chatCriteria = hibernateSession.createCriteria(Single_chat.class);
            chatCriteria.add(Restrictions.or(
                    Restrictions.and(
                            Restrictions.eq("from_user", user),
                            Restrictions.eq("to_user", searchedUser)
                    ),
                    Restrictions.and(
                            Restrictions.eq("from_user", searchedUser),
                            Restrictions.eq("to_user", user)
                    )
            ));
            chatCriteria.addOrder(Order.desc("id"));
            List<Single_chat> chatList = chatCriteria.list();

            if (!chatList.isEmpty()) {
                // has messaged before

                hasMessagedBefore = true;

                for (Single_chat single_chat : chatList) {

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("chatId", single_chat.getId());

                    if (user.getId() == single_chat.getFrom_user().getId()) {
                        //i have send this message
                        //need to get to user details

                        jsonObject.addProperty("userId", single_chat.getTo_user().getId());
                        jsonObject.addProperty("name", single_chat.getTo_user().getFirst_name() + " " + single_chat.getTo_user().getLast_name());
                        jsonObject.addProperty("image", single_chat.getTo_user().getProfile_image());
                        jsonObject.addProperty("onlineStatus", single_chat.getTo_user().getUser_online_status().getStatus());
                        jsonObject.addProperty("about", single_chat.getTo_user().getAbout());

                    } else {
                        //received message
                        //need to get from user details

                        jsonObject.addProperty("userId", single_chat.getFrom_user().getId());
                        jsonObject.addProperty("name", single_chat.getFrom_user().getFirst_name() + " " + single_chat.getFrom_user().getLast_name());
                        jsonObject.addProperty("image", single_chat.getFrom_user().getProfile_image());
                        jsonObject.addProperty("onlineStatus", single_chat.getFrom_user().getUser_online_status().getStatus());
                        jsonObject.addProperty("about", single_chat.getFrom_user().getAbout());

                    }
                    jsonObject.addProperty("messageStatus", single_chat.getMessage_status().getStatus());

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
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
                    jsonObject.addProperty("isFirstTime", false);

                    jsonArray.add(jsonObject);

                }
            } else {
                //first time messaging

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("userId", searchedUser.getId());
                jsonObject.addProperty("name", searchedUser.getFirst_name() + " " + searchedUser.getLast_name());
                jsonObject.addProperty("image", searchedUser.getProfile_image());
                jsonObject.addProperty("onlineStatus", searchedUser.getUser_online_status().getStatus());
                jsonObject.addProperty("about", searchedUser.getAbout());
                jsonObject.addProperty("lastMessage", searchedUser.getFirst_name() + " " + searchedUser.getLast_name());
                jsonObject.addProperty("messageStatus", "");

                SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
                jsonObject.addProperty("datetime", date.format(new Date()));

                jsonObject.addProperty("isFirstTime", true);

                jsonArray.add(jsonObject);

            }

        } else {
            message = "Cannot FInd Registered User";
        }

        Gson gson = new Gson();

        JsonObject jo = new JsonObject();
        jo.addProperty("isFound", isFound);

        if (isFound) {
            jo.addProperty("hasMessagedBefore", hasMessagedBefore);
            jo.add("data", gson.toJsonTree(jsonArray));

        } else {
            jo.addProperty("data", message);

        }

        Response_DTO response_DTO = new Response_DTO(true, gson.toJsonTree(jo));

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));

    }

}
