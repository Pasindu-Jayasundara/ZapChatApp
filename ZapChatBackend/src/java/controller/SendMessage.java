package controller;

import com.google.gson.Gson;
import dto.Response_DTO;
import entity.File;
import entity.Message;
import entity.Message_content_type;
import entity.Message_status;
import entity.Single_chat;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet(name = "SendMessage", urlPatterns = {"/SendMessage"})
public class SendMessage extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        int otherUserId = (int) request.getAttribute("otherUserId");
        String contentType = (String) request.getAttribute("contentType");
        String content = (String) request.getAttribute("content");
        
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        
        User otherUser = (User) hibernateSession.get(User.class, otherUserId);
//        User user = (User) request.getSession().getAttribute("user");
                    User user = gson.fromJson((String) request.getAttribute("user"),User.class);

        Criteria messageStatusCriteria = hibernateSession.createCriteria(Message_status.class);
        messageStatusCriteria.add(Restrictions.eq("status", "Send"));
        Message_status sendStatus = (Message_status) messageStatusCriteria.uniqueResult();
        
        Criteria messageContentTypeCriteria = hibernateSession.createCriteria(Message_content_type.class);
        messageContentTypeCriteria.add(Restrictions.eq("type", contentType));
        Message_content_type contentTypeResult = (Message_content_type) messageContentTypeCriteria.uniqueResult();
        
        // new single chat
        Single_chat single_chat = new Single_chat();
        single_chat.setFrom_user(user);
        single_chat.setTo_user(otherUser);
        single_chat.setDatetime(new Date());
        single_chat.setMessage_status(sendStatus);
        single_chat.setMessage_content_type(contentTypeResult);
        
        int singleChatId = (int) hibernateSession.save(single_chat);
        
        //if content type is message -> add to message
        //if content type is file -> add to file
        Single_chat savedSingleChat = (Single_chat) hibernateSession.get(Single_chat.class, singleChatId);
        if(contentType.equals("Message")){
            
            Message message = new Message();
            message.setMessage(content);
            message.setSingle_chat(savedSingleChat);
            
            hibernateSession.save(message);
            
        }else if(contentType.equals("File")){
            
            File file = new File();
            file.setPath(content);
            file.setSingle_chat(savedSingleChat);
            
            hibernateSession.save(file);
            
        }
        
        hibernateSession.beginTransaction().commit();
        hibernateSession.close();
        
        request.getRequestDispatcher("/SingleChat").include(request, response);

    }

}
