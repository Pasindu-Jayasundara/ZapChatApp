package model;

import com.google.gson.Gson;
import dto.Response_DTO;
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
@WebFilter(urlPatterns = {"/AddNewStatus"})
public class AddNewStatusFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        boolean isInvalid = false;
        String message = "";

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        if (httpServletRequest.getSession().getAttribute("user") == null) {
            isInvalid = true;
            message = "Please Logedin First";

        } else {
            
            System.out.println(httpServletRequest.getParameter("isImage"));
            System.out.println(httpServletRequest.getParameter("isText"));
            
            boolean isImage = Boolean.parseBoolean(httpServletRequest.getParameter("isImage"));
            boolean isText = Boolean.parseBoolean(httpServletRequest.getParameter("isText"));

            if (isImage) {

                String extention = httpServletRequest.getParameter("extention");
                Part img1 = httpServletRequest.getPart("image");

                if (img1 == null) {
                    isInvalid = true;
                    message = "Missing File";

                } else if (extention == null) {
                    isInvalid = true;
                    message = "Missing Extention";

                } else if (!extention.equals(".png") && !extention.equals(".jpg") && !extention.equals(".jpeg")) {
                    isInvalid = true;
                    message = "Unknown Extention";

                } else {

                    request.setAttribute("image", img1);
                    request.setAttribute("extention", extention);
                }

            }

            if (isText) {

                String text = httpServletRequest.getParameter("text");

                if (text == null || text.trim().equals("")) {
                    isInvalid = true;
                    message = "Missing Text";

                } else if (text.length() > 150) {
                    isInvalid = true;
                    message = "Text Too Long";

                } else {

                    request.setAttribute("text", text);
                }
            }
            
            if ((isImage || isText) && !isInvalid) {

                request.setAttribute("isImage", isImage);
                request.setAttribute("isText", isText);
                
                chain.doFilter(request, response);
            }else{
                isInvalid=true;
                message="All False";
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
