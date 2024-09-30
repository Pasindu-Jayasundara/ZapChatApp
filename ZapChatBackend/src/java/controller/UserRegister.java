package controller;

import com.google.gson.Gson;
import dto.Response_DTO;
import entitiy.User;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Session;

@WebServlet(name = "UserRegister", urlPatterns = {"/UserRegister"})
public class UserRegister extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        String first_name = (String) request.getAttribute("first_name");
        String last_name = (String) request.getAttribute("last_name");
        final String email = (String) request.getAttribute("email");
        String password = (String) request.getAttribute("password");

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        

        User user = new User();
//        user.setFirst_name(first_name);
//        user.setLast_name(last_name);
//        user.setPassword(encryptPassword);

//        final String token = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

//        final int addedUserId = (int) hibernateSession.save(user);
//        hibernateSession.beginTransaction().commit();
//
//
//
//        request.getSession().setAttribute("userEmail", email);
//
//        hibernateSession.close();
//
//        Response_DTO response_DTO = new Response_DTO(true, "Please Verify Your Account");
//
//        response.setContentType("application/json");
//        response.getWriter().write(gson.toJson(response_DTO));

    }

}
