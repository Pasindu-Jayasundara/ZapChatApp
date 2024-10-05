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

@WebFilter(urlPatterns = {"/SingleChat"})
public class SingleChatFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        Gson gson = new Gson();
        JsonObject fromJson = gson.fromJson(request.getReader(), JsonObject.class);

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        boolean isSuccess = false;
        String message = "";

        if (httpServletRequest.getSession().getAttribute("user") != null) {

            if (fromJson.has("otherUserId")) {

                String otherUserId = fromJson.get("otherUserId").getAsString();
                if (!Validation.isInteger(otherUserId)) {
                    message = "Invalid Id Type";
                } else {

                    int id = Integer.parseInt(otherUserId);
                    if (id < 1) {
                        message = "Invalid Id Range";
                    } else {
                        isSuccess = true;

                        request.setAttribute("otherUserId", id);
                        chain.doFilter(request, response);
                    }
                }

            } else {
                message = "Missing Id";
            }

        } else {
            message = "Please LogIn";
        }

        if (!isSuccess) {

            Response_DTO response_DTO = new Response_DTO(isSuccess, message);
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(response_DTO));
        }

    }

    @Override
    public void destroy() {
    }

}
