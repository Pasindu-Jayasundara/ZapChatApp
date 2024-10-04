package model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

@WebFilter(urlPatterns = {"/Login"})
public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        Gson gson = new Gson();
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        JsonObject fromJson = gson.fromJson(httpServletRequest.getReader(), JsonObject.class);

        boolean isInvalid = false;
        String errorMessage = "";

        if (!fromJson.has("mobile")) {

            isInvalid = true;
            errorMessage = "Mobile Cannot Be Found";

        } else if (!fromJson.has("password")) {

            isInvalid = true;
            errorMessage = "Password Cannot Be Found";

        } else {

            String mobile = fromJson.get("mobile").getAsString();
            String password = fromJson.get("password").getAsString();

            if (mobile == null || mobile.trim().equals("")) {
                //no mobile
                isInvalid = true;
                errorMessage = "Missing Mobile Number";

            } else if (password == null || password.trim().equals("")) {
                //no password
                isInvalid = true;
                errorMessage = "Missing Password";

            } else {

                if (mobile.length() != 10) {
                    // too long
                    isInvalid = true;
                    errorMessage = "Mobile Number Too Long";

                } else if (password.length() > 20) {
                    //password too long
                    isInvalid = true;
                    errorMessage = "Password Too Long";

                } else if (!Validation.isValidMobile(mobile)) {
                    //invalid mobile format 
                    isInvalid = true;
                    errorMessage = "Invalid Mobile Number Format";

                } else if (!Validation.isValidPassword(password)) {
                    //invalid password
                    isInvalid = true;
                    errorMessage = "Invalid Password Format";

                } else {

                    request.setAttribute("mobile", mobile);
                    request.setAttribute("password", password);

                    chain.doFilter(request, response);
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
