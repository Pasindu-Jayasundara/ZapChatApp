package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entity.File;
import entity.Message;
import entity.Message_status;
import entity.Single_chat;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
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

@WebServlet(name = "SingleChat", urlPatterns = {"/SingleChat"})
public class SingleChat extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        int otherUserId = (int) request.getAttribute("otherUserId");

        JsonObject jsonuser = (JsonObject) request.getAttribute("user");

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        User user = (User) hibernateSession.get(User.class, jsonuser.get("id").getAsInt());

        User otherUser = (User) hibernateSession.get(User.class, otherUserId);

        Criteria messageStatusCriteria = hibernateSession.createCriteria(Message_status.class);
        messageStatusCriteria.add(Restrictions.eq("status", "Read"));
        Message_status readStatus = (Message_status) messageStatusCriteria.uniqueResult();

        Criteria singleChatCriteria = hibernateSession.createCriteria(Single_chat.class);
        singleChatCriteria.add(Restrictions.or(
                Restrictions.and(
                        Restrictions.eq("from_user", user),
                        Restrictions.eq("to_user", otherUser)
                ),
                Restrictions.and(
                        Restrictions.eq("from_user", otherUser),
                        Restrictions.eq("to_user", user)
                )
        ));
        singleChatCriteria.addOrder(Order.asc("datetime"));

        List<Single_chat> SingleChatList = singleChatCriteria.list();
        JsonArray jsonArray = new JsonArray();

        for (Single_chat single_chat : SingleChatList) {

            //get data
            int singleChatId = single_chat.getId();

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", single_chat.getMessage_content_type().getType());

            if (single_chat.getMessage_content_type().getType().equals("Message")) {
                //content is a message

                Message message = (Message) hibernateSession.get(Message.class, singleChatId);

                jsonObject.addProperty("messageId", message.getId());
                jsonObject.addProperty("message", message.getMessage());

            } else if (single_chat.getMessage_content_type().getType().equals("File")) {
                //content is a file

                File file = (File) hibernateSession.get(File.class, singleChatId);

                jsonObject.addProperty("fileId", file.getId());
                jsonObject.addProperty("path", file.getPath());
            }

            SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm");

            jsonObject.addProperty("time", timeSdf.format(single_chat.getDatetime()));
            jsonObject.addProperty("date", dateSdf.format(single_chat.getDatetime()));
            jsonObject.addProperty("messageStatus", single_chat.getMessage_status().getStatus());

            String side = "";
            if (single_chat.getFrom_user().getId() == otherUserId && single_chat.getTo_user().getId() == user.getId()) {
                //received message
                side = "left";
            } else {
                //send message
                side = "right";
            }
            jsonObject.addProperty("side", side);

            jsonArray.add(jsonObject);

            //update message status
            if (user.getId() == single_chat.getTo_user().getId()) {
                //received 

                single_chat.setMessage_status(readStatus);
                hibernateSession.update(single_chat);
            }

        }

        hibernateSession.beginTransaction().commit();
        hibernateSession.close();

        Response_DTO response_DTO = new Response_DTO(true, gson.toJsonTree(jsonArray));
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));

    }

}
