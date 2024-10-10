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
import javax.servlet.http.HttpServletRequest;

@WebFilter(urlPatterns = {"/NewChat"})
public class NewChatFilter implements Filter{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest hsr = (HttpServletRequest) request;
//        User user = (User) hsr.getSession().getAttribute("user");

        boolean isInvalid = false;
        String message = "";

        Gson gson = new Gson();
            JsonObject fromJson = gson.fromJson(request.getReader(), JsonObject.class);

        if (fromJson.has("user")) {


            if (!fromJson.has("mobile")) {
                isInvalid = true;
                message = "Missing Mobile Number";

            } else {

                String mobile = fromJson.get("mobile").getAsString();
                String user = fromJson.get("user").getAsString();

                if (mobile.trim().equals("")) {
                    isInvalid = true;
                    message = "Empty Mobile Number";

                } else if (!Validation.isValidMobile(mobile)) {
                    isInvalid = true;
                    message = "Incorrect Mobile Number";

                }else if (mobile.length()!=10) {
                    isInvalid = true;
                    message = "Incorrect Mobile Number Length";

                } else {

                    isInvalid = false;
                    
                    request.setAttribute("user", mobile);
                    request.setAttribute("mobile", mobile);
                    chain.doFilter(request, response);

                }

            }

        } else {
            isInvalid = true;
            message = "Please LogIn";
        }
        if (isInvalid) {
            Response_DTO response_DTO = new Response_DTO(false, message);

            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(response_DTO));
        }
        
    }

    @Override
    public void destroy() {
    }
    
}
