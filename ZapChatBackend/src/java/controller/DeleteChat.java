package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entity.File;
import entity.Message;
import entity.Single_chat;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "DeleteChat", urlPatterns = {"/DeleteChat"})
public class DeleteChat extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        int chatId = Integer.parseInt((String) request.getAttribute("chatId"));
        JsonObject jsonuser = (JsonObject) request.getAttribute("user");

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        User firstUser = (User) hibernateSession.get(User.class, jsonuser.get("id").getAsInt());

        Single_chat singleChat = (Single_chat) hibernateSession.get(Single_chat.class, chatId);
        if (singleChat != null) {

            int secondUserId = 0;
            if (singleChat.getFrom_user().getId() == firstUser.getId()) {
                //get to user as second user
                secondUserId = singleChat.getTo_user().getId();
            } else {
                //get from user as second user
                secondUserId = singleChat.getFrom_user().getId();
            }

            User secondUser = (User) hibernateSession.get(User.class, secondUserId);

            Criteria chatCriteria = hibernateSession.createCriteria(Single_chat.class);
            chatCriteria.add(Restrictions.or(
                    Restrictions.and(
                            Restrictions.eq("from_user", firstUser),
                            Restrictions.eq("to_user", secondUser)
                    ),
                    Restrictions.and(
                            Restrictions.eq("from_user", secondUser),
                            Restrictions.eq("to_user", firstUser)
                    )
            ));
            List<Single_chat> singleChatList = chatCriteria.list();
            if (!singleChatList.isEmpty()) {

                for (Single_chat single_chat : singleChatList) {

                    if (single_chat.getMessage_content_type().getType().equals("Message")) {

                        Criteria createCriteria = hibernateSession.createCriteria(Message.class);
                        createCriteria.add(Restrictions.eq("single_chat", single_chat));
                        Message msg = (Message) createCriteria.uniqueResult();

                        if (msg != null) {
                            hibernateSession.delete(msg);
                        }

                    } else if (single_chat.getMessage_content_type().getType().equals("File")) {

                        Criteria createCriteria = hibernateSession.createCriteria(File.class);
                        createCriteria.add(Restrictions.eq("single_chat", single_chat));
                        File file = (File) createCriteria.uniqueResult();

                        if (file != null) {
                            hibernateSession.delete(file);
                        }

                    }

                    hibernateSession.delete(single_chat);

                }

                hibernateSession.beginTransaction().commit();
            }

        }
        
        Response_DTO response_DTO = new Response_DTO(true, "Success");

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
    }

}
