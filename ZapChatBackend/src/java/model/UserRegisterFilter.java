package model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entity.User;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebFilter(urlPatterns = {"/Register"})
public class UserRegisterFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        Gson gson = new Gson();
        JsonObject fromJson = gson.fromJson(request.getReader(), JsonObject.class);

        boolean isInvalid = false;
        String errorMessage = "";

        if (!fromJson.has("fName")) {

            isInvalid = true;
            errorMessage = "Missing First Name";

        } else if (!fromJson.has("lName")) {

            isInvalid = true;
            errorMessage = "Missing Last Name";

        } else if (!fromJson.has("mobile")) {

            isInvalid = true;
            errorMessage = "Missing Mobile Number";

        } else if (!fromJson.has("password")) {

            isInvalid = true;
            errorMessage = "Missing Password";

        } else if (!fromJson.has("reTypePassword")) {

            isInvalid = true;
            errorMessage = "Missing Re-Type Password";

        } else {

            String fName = fromJson.get("fName").getAsString();
            String lName = fromJson.get("lName").getAsString();
            String mobile = fromJson.get("mobile").getAsString();
            String password = fromJson.get("password").getAsString();
            String reTypePassword = fromJson.get("reTypePassword").getAsString();

            if (fName.trim().equals("")) {
                // no first name
                isInvalid = true;
                errorMessage = "First Name Cannot Be Empty";

            } else if (lName.trim().equals("")) {
                //no last name
                isInvalid = true;
                errorMessage = "Last Name Cannot Be Empty";

            } else if (mobile.trim().equals("")) {
                //no mobile
                isInvalid = true;
                errorMessage = "Mobile Number Cannot Be Empty";

            } else if (password.trim().equals("")) {
                //no password
               isInvalid = true;
                errorMessage = "Password Cannot Be Empty";

            } else if (reTypePassword.trim().equals("")) {
                //no retype password
                isInvalid = true;
                errorMessage = "Re-Type Password Cannot Be Empty";

            } else {

                if (fName.length() > 45) {
                    //first name too long
                    isInvalid = true;
                    errorMessage = "First Name Too Long";

                } else if (lName.length() > 45) {
                    //last  name too long
                    isInvalid = true;
                    errorMessage = "Last Name Too Long";

                } else if (mobile.length() > 10) {
                    //mobile too long
                    isInvalid = true;
                    errorMessage = "Mobile Number Too Long";

                } else if (password.length() > 20) {
                    //password too long
                    isInvalid = true;
                    errorMessage = "Password Too Long";

                } else if (reTypePassword.length() > 20) {
                    //retype password too long
                    isInvalid = true;
                    errorMessage = "Re-Type Password Too Long";

                } else {

                    if (!Validation.isValidMobile(mobile)) {
                        //invalid format 
                        isInvalid = true;
                        errorMessage = "Invalid Mobile Number Format";

                    } else if (!Validation.isValidName(fName.trim())) {
                        //invalid first name
                        isInvalid = true;
                        errorMessage = "Invalid First Name Format";

                    } else if (!Validation.isValidName(lName.trim())) {
                        //invalid last name
                        isInvalid = true;
                        errorMessage = "Invalid Last Name Format";

                    } else if (!Validation.isValidPassword(password)) {
                        //invalid password
                        isInvalid = true;
                        errorMessage = "Invalid Password Format";

                    } else if (!Validation.isValidPassword(reTypePassword)) {
                        //invalid retype password
                        isInvalid = true;
                        errorMessage = "Invalid Re-Typed Password Format";

                    } else {

                        if (!password.equals(reTypePassword)) {
                            // 2 passwords dont match
                            isInvalid = true;
                            errorMessage = "Miss-match Passwords";

                        } else {
                            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

                            //find user
                            Criteria userCriteria = hibernateSession.createCriteria(User.class);
                            userCriteria.add(Restrictions.eq("mobile", mobile));

                            if (!userCriteria.list().isEmpty()) {
                                // already registered
                                Response_DTO response_DTO = new Response_DTO(false, "User Already Registered");

                                hibernateSession.close();

                                response.setContentType("application/json");
                                response.getWriter().write(gson.toJson(response_DTO));
                            } else {

                                request.setAttribute("first_name", fName);
                                request.setAttribute("last_name", lName);
                                request.setAttribute("mobile", mobile);
                                request.setAttribute("password", password);

                                chain.doFilter(request, response);
                            }

                        }
                    }

                }
            }
        }
        if (isInvalid) {
            Response_DTO response_DTO = new Response_DTO(false, errorMessage);

            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(response_DTO));
        }

    }

    @Override
    public void destroy() {
    }

}
