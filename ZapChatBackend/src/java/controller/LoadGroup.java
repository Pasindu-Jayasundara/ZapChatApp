package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entity.Group_chat;
import entity.Group_member;
import entity.Group_message;
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

@WebServlet(name = "LoadGroup", urlPatterns = {"/LoadGroup"})
public class LoadGroup extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        boolean isSearch = (boolean) request.getAttribute("isSearch");
        String searchText = (String) request.getAttribute("searchText");

        JsonObject jsonuser = (JsonObject) request.getAttribute("user");

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        User user = (User) hibernateSession.get(User.class, jsonuser.get("id").getAsInt());

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
                    String groupName = groupMember.getGroup_table().getName().toLowerCase();

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

                Criteria groupChatCriteria = hibernateSession.createCriteria(Group_chat.class);
                groupChatCriteria.add(Restrictions.eq("group_table", groupMember.getGroup_table()));
                groupChatCriteria.addOrder(Order.desc("id"));
                groupChatCriteria.setMaxResults(1);
                Group_chat groupChat = (Group_chat) groupChatCriteria.uniqueResult();

                if (groupChat != null) {

                    if (!isFound) {
                        isFound = true;
                    }

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("groupId", groupMember.getGroup_table().getId());
                    jsonObject.addProperty("name", groupMember.getGroup_table().getName());
                    jsonObject.addProperty("image", groupMember.getGroup_table().getImage_path());

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    SimpleDateFormat time = new SimpleDateFormat("HH:mm");
                    SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");

                    boolean equal = date.format(new Date()).equals(date.format(groupChat.getDatetime()));
                    if (equal) {
                        //same day
                        jsonObject.addProperty("datetime", time.format(groupChat.getDatetime()));

                    } else {
                        jsonObject.addProperty("datetime", date.format(groupChat.getDatetime()));

                    }

                    if (groupChat.getMessage_content_type().getType().equals("Message")) {

                        Criteria msgCriteria = hibernateSession.createCriteria(Group_message.class);
                        msgCriteria.add(Restrictions.eq("group_chat", groupChat));

                        Group_message msg = (Group_message) msgCriteria.uniqueResult();

                        jsonObject.addProperty("lastMessage", msg.getMessage());

                    } else {
                        jsonObject.addProperty("lastMessage", "File Content");
                    }

                    Criteria memberCountCriteria = hibernateSession.createCriteria(Group_member.class);
                    memberCountCriteria.add(Restrictions.eq("group_table", groupMember.getGroup_table()));
                    memberCountCriteria.setProjection(Projections.count("id"));
                    Long count = (Long) memberCountCriteria.uniqueResult();

                    jsonObject.addProperty("members", count);

                    Criteria isNewCriteria = hibernateSession.createCriteria(Group_member.class);
                    isNewCriteria.add(Restrictions.and(
                            Restrictions.eq("user", user),
                            Restrictions.eq("group_table", groupMember.getGroup_table())
                    ));
                    Group_member isNew = (Group_member) isNewCriteria.uniqueResult();

                    if (isNew != null) {
                        jsonObject.addProperty("isNew", false);
                    } else {
                        jsonObject.addProperty("isNew", true);
                    }

                    jsonArray.add(jsonObject);
                }
            }

        }

        JsonObject jo = new JsonObject();
        jo.addProperty("isFound", isFound);
        jo.add("data", gson.toJsonTree(jsonArray));

        Response_DTO response_DTO = new Response_DTO(true, gson.toJsonTree(jo));

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));

    }

}
