package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entity.File;
import entity.Group_chat;
import entity.Group_chat_read;
import entity.Group_file;
import entity.Group_member;
import entity.Group_message;
import entity.Group_table;
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

@WebServlet(name = "SingleGroup", urlPatterns = {"/SingleGroup"})
public class SingleGroup extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int groupId = (int) request.getAttribute("groupId");
        User user = (User) request.getSession().getAttribute("user");

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        Group_table group = (Group_table) hibernateSession.get(Group_table.class, groupId);
        JsonArray jsonArray = new JsonArray();

        if (group != null) {
            // has a group

            Criteria groupChatCriteria = hibernateSession.createCriteria(Group_chat.class);
            groupChatCriteria.add(Restrictions.eq("group_table", group));
            List<Group_chat> groupChatList = groupChatCriteria.list();

            if (!groupChatList.isEmpty()) {
                // has chats

                for (Group_chat groupChat : groupChatList) {

                    int chatId = groupChat.getId();

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("type", groupChat.getMessage_content_type().getType());

                    if (groupChat.getMessage_content_type().getType().equals("Message")) {
                        //content is a message

                        Group_message message = (Group_message) hibernateSession.get(Group_message.class, chatId);

                        jsonObject.addProperty("messageId", message.getId());
                        jsonObject.addProperty("message", message.getMessage());

                    } else if (groupChat.getMessage_content_type().getType().equals("File")) {
                        //content is a file

                        Group_file file = (Group_file) hibernateSession.get(Group_file.class, chatId);

                        jsonObject.addProperty("fileId", file.getId());
                        jsonObject.addProperty("path", file.getPath());
                    }

                    SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm");

                    jsonObject.addProperty("time", timeSdf.format(groupChat.getDatetime()));
                    jsonObject.addProperty("date", dateSdf.format(groupChat.getDatetime()));
                    jsonObject.addProperty("messageStatus", groupChat.getMessage_status().getStatus());

                    String side = "";
                    if (groupChat.getGroup_member().getUser().getId() != user.getId()) {
                        //received message
                        side = "left";

                        jsonObject.addProperty("senderName", groupChat.getGroup_member().getUser().getFirst_name() + " " + groupChat.getGroup_member().getUser().getLast_name());
                        jsonObject.addProperty("senderImg", groupChat.getGroup_member().getUser().getProfile_image());
                    } else {
                        //send message
                        side = "right";
                    }
                    jsonObject.addProperty("side", side);

                    jsonArray.add(jsonObject);

                    //update message status
                    if (user.getId() != groupChat.getGroup_member().getUser().getId()) {
                        //received 

                        Criteria groupMemberCriteria = hibernateSession.createCriteria(Group_member.class);
                        groupMemberCriteria.add(Restrictions.and(
                                Restrictions.eq("user", user),
                                Restrictions.eq("group_table", group)
                        ));
                        Group_member group_member = (Group_member) groupMemberCriteria.uniqueResult();

                        Criteria chatReadCriteria = hibernateSession.createCriteria(Group_chat_read.class);
                        chatReadCriteria.add(Restrictions.and(
                                Restrictions.eq("group_member", group_member),
                                Restrictions.eq("group_chat", groupChat)
                        ));
                        Group_chat_read groupchat = (Group_chat_read) chatReadCriteria.uniqueResult();

                        if (groupchat == null) {
                            //havent read yet

                            Group_chat_read read = new Group_chat_read();
                            read.setGroup_chat(groupChat);
                            read.setGroup_member(group_member);

                            hibernateSession.save(read);
                            hibernateSession.beginTransaction().commit();

                        }

                    }

                }

            }

        }

        Gson gson = new Gson();
        Response_DTO response_DTO = new Response_DTO(true, gson.toJsonTree(jsonArray));
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));

    }

}
