package model;

import com.google.gson.Gson;
import dto.Response_DTO;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
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
import org.kohsuke.rngom.util.Uri;

//@MultipartConfig
@WebFilter(urlPatterns = {"/Profile"})
public class ProfileFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        boolean isInvalid = false;
        String message = "";

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String about = httpServletRequest.getParameter("about");
        boolean isNewImage = Boolean.parseBoolean(httpServletRequest.getParameter("isNewImage"));
        String extention = httpServletRequest.getParameter("extention");

        Part img1 = null;
        if (isNewImage) {
            img1 = httpServletRequest.getPart("image");
        }

        String user = httpServletRequest.getParameter("user");
        if (user == null) {
            isInvalid = true;
            message = "Please Logedin First";

        } else if (about == null || about.trim().equals("")) {
            isInvalid = true;
            message = "Missing About";

        } else {

            if (isNewImage) {
                if (img1 == null) {
                    isInvalid = true;
                    message = "Missing File";

                } else if (extention == null) {
                    isInvalid = true;
                    message = "Missing Extention";

                } else {

                    if (extention.equals(".png") || extention.equals(".jpg") || extention.equals(".jpeg")) {
                        if (about.length() > 45) {
                            isInvalid = true;
                            message = "About Too Long";

                        }

                    } else {
                        isInvalid = true;
                        message = "Invalid Extention";
                    }
                }
            }

            if (!isInvalid) {
                if (isNewImage) {

                    request.setAttribute("image", img1);
                    request.setAttribute("extention", extention);
                }

                request.setAttribute("about", about);
                request.setAttribute("isNewImage", isNewImage);
                request.setAttribute("user", user);

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
