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

@WebFilter(urlPatterns = {"/DeleteChat"})
public class DeleteChatFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        boolean isInvalid = false;
        String message = "";

        Gson gson = new Gson();
        JsonObject fromJson = gson.fromJson(request.getReader(), JsonObject.class);

        if (fromJson.has("user")) {

            if (!fromJson.has("chatId")) {
                isInvalid = true;
                message = "Missing Chat Id";

            } else {

                String chatId = fromJson.get("chatId").getAsString();
                JsonObject user = fromJson.get("user").getAsJsonObject();

                if (!Validation.isInteger(chatId)) {
                    isInvalid = true;
                    message = "No a Id Id";

                } else {

                    request.setAttribute("chatId", chatId);
                    request.setAttribute("user", user);

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
