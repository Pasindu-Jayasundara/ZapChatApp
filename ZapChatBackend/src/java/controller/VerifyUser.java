package controller;

import com.google.gson.Gson;
import dto.Response_DTO;
import entitiy.User;
import entitiy.User_online_status;
import entitiy.User_verified_status;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "VerifyUser", urlPatterns = {"/VerifyUser"})
public class VerifyUser extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        User user = (User) request.getSession(false).getAttribute("user");

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        Criteria verifiedCriteria = hibernateSession.createCriteria(User_verified_status.class);
        verifiedCriteria.add(Restrictions.eq("status", "Verified"));
        User_verified_status user_verified_status = (User_verified_status) verifiedCriteria.uniqueResult();

        Criteria onlineCriteria = hibernateSession.createCriteria(User_online_status.class);
        onlineCriteria.add(Restrictions.eq("status", "Online"));
        User_online_status online = (User_online_status) onlineCriteria.uniqueResult();

        user.setUser_verified_status(user_verified_status);
        user.setUser_online_status(online);
        hibernateSession.save(user);

        hibernateSession.beginTransaction().commit();
        hibernateSession.close();

        Gson gson = new Gson();
        Response_DTO response_DTO = new Response_DTO(true, "success");
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));

    }

}
