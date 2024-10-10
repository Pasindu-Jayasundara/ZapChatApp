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
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

@MultipartConfig
@WebFilter(urlPatterns = {"/AddNewGroup"})
public class AddNewGroupFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        boolean isInvalid = false;
        String message = "";

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String groupName = httpServletRequest.getParameter("groupName");
        String extention = httpServletRequest.getParameter("extention");
        String user = httpServletRequest.getParameter("user");
        Part img1 = httpServletRequest.getPart("image");

        if (user == null) {
            isInvalid = true;
            message = "Please Logedin First";

        } else if (groupName == null) {
            isInvalid = true;
            message = "Missing Group Name";

        } else if (extention == null) {
            isInvalid = true;
            message = "Missing Extention";

        } else if (img1 == null) {
            isInvalid = true;
            message = "Missing Group Icon";

        } else {

            if (groupName.trim().equals("")) {
                isInvalid = true;
                message = "Empty Group Name";

            } else if (extention.trim().equals("")) {
                isInvalid = true;
                message = "Empty Extention";

            } else if (img1 == null) {
                isInvalid = true;
                message = "Missing File";

            } else {

                if (extention.equals(".png") || extention.equals(".jpg") || extention.equals(".jpeg")) {
                    if (groupName.length() > 45) {
                        isInvalid = true;
                        message = "Group Name Too Long";

                    }

                } else {
                    isInvalid = true;
                    message = "Invalid Extention";
                }
            }

            if (!isInvalid) {
                request.setAttribute("user", user);
                request.setAttribute("image", img1);
                request.setAttribute("extention", extention);
                request.setAttribute("groupName", groupName);

                chain.doFilter(request, response);
            }

        }

        if (isInvalid) {
            Response_DTO response_DTO = new Response_DTO(false, message);

            Gson gson = new Gson();
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(response_DTO));
        }

    }

    @Override
    public void destroy() {
    }

}
