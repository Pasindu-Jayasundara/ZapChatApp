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

@WebFilter(urlPatterns = {"/SendMessage"})
public class SendMessageFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        Gson gson = new Gson();
        JsonObject fromJson = gson.fromJson(request.getReader(), JsonObject.class);

        boolean isSuccess = false;
        String message = "";

        if (fromJson.has("user")) {

            if (!fromJson.has("otherUserId")) {
                message = "Missing Id";

            } else if (!fromJson.has("contentType")) {
                message = "Missing Content Type";

            } else if (!fromJson.has("content")) {
                message = "Missing Content";

            } else {

                String otherUserId = fromJson.get("otherUserId").getAsString();
                String contentType = fromJson.get("contentType").getAsString();
                String content = fromJson.get("content").getAsString();
                String user = fromJson.get("user").getAsString();

                if (!Validation.isInteger(otherUserId)) {
                    message = "Invalid Id Type";

                } else if (!Validation.isValidName(contentType)) {
                    message = "Invalid Content Type Type";

                } else if (!contentType.equals("Message") && !contentType.equals("File")) {
                    message = "Invalid Contnt Type";

                } else {

                    boolean isOk = false;
                    if (contentType.equals("Message")) {
                        isOk = true;
                    } else if (contentType.equals("File")) {
                        isOk = true;
                    }

                    if (isOk) {

                        int id = Integer.parseInt(otherUserId);
                        if (id < 1) {
                            message = "Invalid Id Range";
                        } else {

                            isSuccess = true;

                            request.setAttribute("otherUserId", id);
                            request.setAttribute("contentType", contentType);
                            request.setAttribute("content", content);
                            request.setAttribute("user", user);

                            chain.doFilter(request, response);
                        }

                    } else {
                        message = "Invalid Content";
                    }

                }
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
