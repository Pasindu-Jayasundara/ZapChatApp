package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entitiy.User;
import entitiy.User_online_status;
import entitiy.User_verified_status;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.SendSMS;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "Register", urlPatterns = {"/Register"})
public class UserRegister extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        boolean isSuccess = true;
        String message = "";

        Gson gson = new Gson();

        String first_name = (String) request.getAttribute("first_name");
        String last_name = (String) request.getAttribute("last_name");
        final String mobile = (String) request.getAttribute("mobile");
        String password = (String) request.getAttribute("password");

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        Criteria userCriteria = hibernateSession.createCriteria(User.class);
        userCriteria.add(Restrictions.eq("mobile", mobile));
        User user1 = (User) userCriteria.uniqueResult();

        if (user1 == null) {

            Criteria offlineCriteria = hibernateSession.createCriteria(User_online_status.class);
            offlineCriteria.add(Restrictions.eq("status", "Offline"));
            User_online_status offlineStatus = (User_online_status) offlineCriteria.uniqueResult();

            Criteria notVerifiedCriteria = hibernateSession.createCriteria(User_verified_status.class);
            notVerifiedCriteria.add(Restrictions.eq("status", "Not-Verified"));
            User_verified_status notverifystatus = (User_verified_status) notVerifiedCriteria.uniqueResult();

            final int otp = Integer.parseInt(UUID.randomUUID().toString().replace("-", "").substring(0, 8));

            User user = new User();
            user.setFirst_name(first_name);
            user.setLast_name(last_name);
            user.setMobile(mobile);
            user.setPassword(password);
            user.setOtp(otp);
            user.setRegistered_datetime(new Date());
            user.setUser_online_status(offlineStatus);
            user.setUser_verified_status(notverifystatus);
            user.setAbout(first_name+" "+last_name);
            user.setProfile_image("../assets/images/profileDefault.png");

            request.getSession().setAttribute("user", user);

            new Thread(new Runnable() {
                @Override
                public void run() {

                    //SendSMS.send(mobile, "Your ZapChat OTP Code is:"+otp);
                }
            }).start();

            message = request.getSession().getId();
        } else {
            isSuccess = false;
            message = "Already Registered Mobile Number";
        }

        hibernateSession.close();

        Response_DTO response_DTO = new Response_DTO(isSuccess, message);
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
    }

}
