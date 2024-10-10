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

@WebFilter(urlPatterns = {"/Home"})
public class HomeFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

//        HttpServletRequest hsr = (HttpServletRequest) request;
//        User user = (User) hsr.getSession().getAttribute("user");

        boolean isInvalid = false;
        String message = "";

        Gson gson = new Gson();
            JsonObject fromJson = gson.fromJson(request.getReader(), JsonObject.class);

        if (fromJson.has("user")) {


            if (!fromJson.has("searchText")) {
                isInvalid = true;
                message = "Missing Search Text";

            } else if (!fromJson.has("category")) {
                isInvalid = true;
                message = "Missing Category";

            } else {

                String category = fromJson.get("category").getAsString();
                String searchText = fromJson.get("searchText").getAsString();
                JsonObject user =  fromJson.get("user").getAsJsonObject();

                if (category.trim().equals("")) {
                    isInvalid = true;
                    message = "Empty Category";

                } else {

                    if (!searchText.trim().equals("")) {
                        request.setAttribute("searchText", searchText);
                        request.setAttribute("isSearch", true);

                    } else {
                        request.setAttribute("isSearch", false);

                    }
                    request.setAttribute("category", category);
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
