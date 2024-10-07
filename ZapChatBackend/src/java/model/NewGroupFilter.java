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

@WebFilter(urlPatterns = {"/NewGroup"})
public class NewGroupFilter implements Filter{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest hsr = (HttpServletRequest) request;
        User user = (User) hsr.getSession().getAttribute("user");

        boolean isInvalid = false;
        String message = "";

        Gson gson = new Gson();

        if (user != null) {

            JsonObject fromJson = gson.fromJson(request.getReader(), JsonObject.class);

            if (!fromJson.has("name")) {
                isInvalid = true;
                message = "Missing Group Name";

            } else {

                String groupName = fromJson.get("name").getAsString();

                if (groupName.trim().equals("")) {
                    isInvalid = true;
                    message = "Empty Group Name";

                } else if (groupName.length() < 45) {
                    isInvalid = true;
                    message = "Incorrect Group Name Length";

                } else {

                    isInvalid = false;
                    
                    request.setAttribute("name", groupName);
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
