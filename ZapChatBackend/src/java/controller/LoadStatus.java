package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entity.Group_chat;
import entity.Group_member;
import entity.Group_message;
import entity.Single_chat;
import entity.Status;
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

@WebServlet(name = "LoadStatus", urlPatterns = {"/LoadStatus"})
public class LoadStatus extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        boolean isSearch = (boolean) request.getAttribute("isSearch");
        String searchText = (String) request.getAttribute("searchText");
        User user = (User) request.getSession().getAttribute("user");

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        Criteria chatCriteria = hibernateSession.createCriteria(Single_chat.class);
        chatCriteria.add(Restrictions.or(
                Restrictions.eq("from_user", user),
                Restrictions.eq("to_user", user)
        ));
        chatCriteria.addOrder(Order.desc("id"));
        List<Single_chat> singleChatList = chatCriteria.list();

        boolean isFound = false;

        ArrayList<Status> statusArray = new ArrayList<>();

        if (!singleChatList.isEmpty()) {
            //has chats

            ArrayList<User> userArray = new ArrayList<>();
            for (Single_chat single_chat : singleChatList) {

                if (user.getId() == single_chat.getFrom_user().getId()) {
                    //i have send this message

                    userArray.add(single_chat.getTo_user());
                } else {
                    userArray.add(single_chat.getFrom_user());
                }

            }

            if (!userArray.isEmpty()) {

                ArrayList<User> searchUser = new ArrayList<>();

                if (isSearch) {
                    String searchLower = searchText.toLowerCase();

                    for (User user2 : userArray) {

                        String firstName = user2.getFirst_name().toLowerCase();
                        String lastName = user2.getLast_name().toLowerCase();

                        if (firstName.contains(searchLower) || lastName.contains(searchLower)) {

                            searchUser.add(user2);
                        }

                    }
                }

                ArrayList<User> searchFrom;
                if (isSearch) {
                    searchFrom = searchUser;
                } else {
                    searchFrom = userArray;
                }

                for (User user1 : searchFrom) {

                    Criteria statusCriteria = hibernateSession.createCriteria(Status.class);
                    statusCriteria.add(Restrictions.eq("user", user1));
                    statusCriteria.addOrder(Order.desc("datetime"));

                    Status status = (Status) statusCriteria.uniqueResult();
                    if (status != null) {
                        statusArray.add(status);

                        if (!isFound) {
                            isFound = true;
                        }
                    }

                }
            }

        }

        Gson gson = new Gson();

        JsonObject jo = new JsonObject();
        jo.addProperty("isFound", isFound);
        jo.add("data", gson.toJsonTree(statusArray));

        Response_DTO response_DTO = new Response_DTO(true, gson.toJsonTree(jo));

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));

    }

}
