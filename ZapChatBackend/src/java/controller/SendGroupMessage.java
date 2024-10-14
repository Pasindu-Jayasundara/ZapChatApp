package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Group_chat;
import entity.Group_file;
import entity.Group_member;
import entity.Group_message;
import entity.Group_table;
import entity.Message_content_type;
import entity.Message_status;
import entity.User;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SendGroupMessage", urlPatterns = {"/SendGroupMessage"})
public class SendGroupMessage extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        int groupId = (int) request.getAttribute("groupId");
        String contentType = (String) request.getAttribute("contentType");
        String content = (String) request.getAttribute("content");

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        Group_table group = (Group_table) hibernateSession.get(Group_table.class, groupId);
        User user = gson.fromJson((JsonObject) request.getAttribute("user"), User.class);

        Criteria groupMemberCriteria = hibernateSession.createCriteria(Group_member.class);
        groupMemberCriteria.add(Restrictions.and(
                Restrictions.eq("user", user),
                Restrictions.eq("group_table", group)
        ));
        Group_member group_member = (Group_member) groupMemberCriteria.uniqueResult();

        Criteria messageStatusCriteria = hibernateSession.createCriteria(Message_status.class);
        messageStatusCriteria.add(Restrictions.eq("status", "Send"));
        Message_status sendStatus = (Message_status) messageStatusCriteria.uniqueResult();

        Criteria messageContentTypeCriteria = hibernateSession.createCriteria(Message_content_type.class);
        messageContentTypeCriteria.add(Restrictions.eq("type", contentType));
        Message_content_type contentTypeResult = (Message_content_type) messageContentTypeCriteria.uniqueResult();

        // new group chat
        Group_chat groupChat = new Group_chat();
        groupChat.setGroup_member(group_member);
        groupChat.setDatetime(new Date());
        groupChat.setMessage_status(sendStatus);
        groupChat.setMessage_content_type(contentTypeResult);
        groupChat.setGroup_table(group);

        int groupChatId = (int) hibernateSession.save(groupChat);

        //if content type is message -> add to message
        //if content type is file -> add to file
        Group_chat savedGroupChat = (Group_chat) hibernateSession.get(Group_chat.class, groupChatId);
        if (contentType.equals("Message")) {

            Group_message message = new Group_message();
            message.setMessage(content);
            message.setGroup_chat(savedGroupChat);

            hibernateSession.save(message);

        } else if (contentType.equals("File")) {

            Group_file file = new Group_file();
            file.setPath(content);
            file.setGroup_chat(savedGroupChat);

            hibernateSession.save(file);

        }

        hibernateSession.beginTransaction().commit();
        hibernateSession.close();

        request.getRequestDispatcher("/SingleGroup").include(request, response);

    }

}
