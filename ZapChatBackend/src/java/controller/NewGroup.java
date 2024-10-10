package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entity.Group_table;
import entity.Group_chat;
import entity.Group_file;
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
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "NewGroup", urlPatterns = {"/NewGroup"})
public class NewGroup extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        
        String groupName = (String) request.getAttribute("name");
//        User user = (User) request.getSession().getAttribute("user");
                    User user = gson.fromJson((String) request.getAttribute("user"),User.class);

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        boolean isFound = false;
        String message = "";

        //check if there is a group avaliable for this group name
        Criteria groupCriteria = hibernateSession.createCriteria(Group_table.class);
        groupCriteria.add(Restrictions.like("name", groupName,MatchMode.ANYWHERE));
        List<Group_table> searchedGroupList = groupCriteria.list();

        ArrayList<JsonObject> groupArray = new ArrayList<>();

        if (!searchedGroupList.isEmpty()) {
            //search group avaliable

            isFound=true;
            
            for (Group_table group : searchedGroupList) {

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("groupId", group.getId());
                jsonObject.addProperty("name", group.getName());
                jsonObject.addProperty("image", group.getImage_path());

                Criteria memberCriteria = hibernateSession.createCriteria(Group_member.class);
                memberCriteria.add(Restrictions.and(
                        Restrictions.eq("user", user),
                        Restrictions.eq("group_table", group)
                ));
                Group_member searchedGroup = (Group_member) memberCriteria.uniqueResult();

                if (searchedGroup != null) {
                    //this user is a memebr of this group
                    jsonObject.addProperty("isNew", false);

                } else {
                    //not a member
                    jsonObject.addProperty("isNew", true);

                }

                Criteria groupChatCriteria = hibernateSession.createCriteria(Group_chat.class);
                groupChatCriteria.add(Restrictions.eq("group_table", group));
                groupChatCriteria.addOrder(Order.desc("id"));
                groupChatCriteria.setMaxResults(1);
                Group_chat groupChat = (Group_chat) groupChatCriteria.uniqueResult();

                if (groupChat != null) {
                    //group has chats

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

                        Criteria groupMessageCriteria = hibernateSession.createCriteria(Group_message.class);
                        groupMessageCriteria.add(Restrictions.eq("group_chat", groupChat));
                        Group_message groupMessage = (Group_message) groupMessageCriteria.uniqueResult();

                        jsonObject.addProperty("lastMessage", groupMessage.getMessage());

                    } else {
                        jsonObject.addProperty("lastMessage", "File Content");

                    }

                } else {
                    jsonObject.addProperty("lastMessage", "Start Messaging Now");
                }

                Criteria groupMemberCriteria = hibernateSession.createCriteria(Group_member.class);
                groupMemberCriteria.add(Restrictions.eq("group_table", group));
                groupMemberCriteria.setProjection(Projections.count("id"));
                Long memeberCount = (Long) groupMemberCriteria.uniqueResult();

                jsonObject.addProperty("members", memeberCount);
                
                groupArray.add(jsonObject);

            }

        } else {
            message = "Cannot Find a Relavant Group";
        }

        JsonObject jo = new JsonObject();
        jo.addProperty("isFound", isFound);

        if (isFound) {
            jo.add("data", gson.toJsonTree(groupArray));

        } else {
            jo.addProperty("data", message);
        }

        Response_DTO response_DTO = new Response_DTO(true, gson.toJsonTree(jo));

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));

    }

}
