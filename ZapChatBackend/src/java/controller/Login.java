package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entity.User;
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

@WebServlet(name = "Login", urlPatterns = {"/Login"})
public class Login extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String mobile = (String) request.getAttribute("mobile");
        String password = (String) request.getAttribute("password");

        String message = "";
        boolean isSuccess = false;

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        Criteria userCriteria = hibernateSession.createCriteria(User.class);
        userCriteria.add(Restrictions.and(
                Restrictions.eq("mobile", mobile),
                Restrictions.eq("password", password))
        );
        User user = (User) userCriteria.uniqueResult();

        if (user != null) {
            if (user.getUser_verified_status().getStatus().equals("Not-Verified")) {
                //not verified
                message = "Not Verified";

            } else {
                //verified
//                request.getSession().setAttribute("user", user);

                message = "Login Success";
                isSuccess = true;
            }
        } else {
            message = "Invalid Details";
        }

        String sessionId = "";
        JsonObject jo = new JsonObject();
        Gson gson = new Gson();

        if (isSuccess) {
//            sessionId = request.getSession().getId();
//            jo.addProperty("sessionId", sessionId);


            jo.add("user", gson.toJsonTree(user));
            
            jo.addProperty("profileImage", user.getProfile_image());
            jo.addProperty("profileAbout", user.getAbout());
        } else {
            jo.addProperty("msg", message);
        }

        hibernateSession.close();

        Response_DTO response_DTO = new Response_DTO(isSuccess, gson.toJsonTree(jo));

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));

    }

}
