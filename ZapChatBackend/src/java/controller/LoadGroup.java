package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entity.Group;
import entity.Group_chat;
import entity.Group_member;
import entity.Group_message;
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

@WebServlet(name = "LoadGroup", urlPatterns = {"/LoadGroup"})
public class LoadGroup extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        boolean isSearch = (boolean) request.getAttribute("isSearch");
        String searchText = (String) request.getAttribute("searchText");
        User user = (User) request.getSession().getAttribute("user");

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        Criteria groupMemberCriteria = hibernateSession.createCriteria(Group_member.class);
        groupMemberCriteria.add(Restrictions.eq("user", user));
        groupMemberCriteria.addOrder(Order.desc("id"));

        List<Group_member> groupMemberList = groupMemberCriteria.list();

        ArrayList<Group_member> memberArray = new ArrayList<>();
        ArrayList<Group_member> searchGroupArray = new ArrayList<>();

        boolean isFound = false;

        JsonArray jsonArray = new JsonArray();

        if (!groupMemberList.isEmpty()) {
            // member of a group

            for (Group_member groupMember : groupMemberList) {
                memberArray.add(groupMember);
            }

            if (isSearch) {
                String searchLower = searchText.toLowerCase();

                for (Group_member groupMember : memberArray) {
                    String groupName = groupMember.getGroup().getName().toLowerCase();

                    if (groupName.contains(searchLower)) {
                        searchGroupArray.add(groupMember);
                    }

                }
            }

            ArrayList<Group_member> searchFrom;
            if (isSearch) {
                searchFrom = searchGroupArray;
            } else {
                searchFrom = memberArray;
            }
            for (Group_member groupMember : searchFrom) {
                
                if (!isFound) {
                    isFound = true;
                }
                
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("chatId", groupMember.getGroup().getId());
                jsonObject.addProperty("name", groupMember.getGroup().getName());

                Criteria groupChatCriteria = hibernateSession.createCriteria(Group_chat.class);
                groupChatCriteria.add(Restrictions.eq("group_member", groupMember));
                groupChatCriteria.addOrder(Order.desc("id"));
                groupChatCriteria.setMaxResults(1);
                Group_chat groupChat = (Group_chat) groupChatCriteria.uniqueResult();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");

                boolean equal = date.format(new Date()).equals(date.format(groupChat.getDatetime()));
                if (equal) {
                    //same day
                    jsonObject.addProperty("datetime", time.format(groupChat.getDatetime()));

                } else {
                    jsonObject.addProperty("datetime", sdf.format(groupChat.getDatetime()));

                }

                if (groupChat.getMessage_content_type().getType().equals("Message")) {

                    Criteria msgCriteria = hibernateSession.createCriteria(Group_message.class);
                    msgCriteria.add(Restrictions.eq("group_chat", groupChat));

                    Group_message msg = (Group_message) msgCriteria.uniqueResult();

                    jsonObject.addProperty("lastMessage", msg.getMessage());

                } else {
                    jsonObject.addProperty("lastMessage", "File Content");

                }
                jsonArray.add(jsonObject);
            }

        }

        Gson gson = new Gson();

        JsonObject jo = new JsonObject();
        jo.addProperty("isFound", isFound);
        jo.add("data", gson.toJsonTree(jsonArray));

        Response_DTO response_DTO = new Response_DTO(true, gson.toJsonTree(jo));

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));

    }

}
