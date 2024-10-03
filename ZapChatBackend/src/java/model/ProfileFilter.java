package model;

import com.google.gson.Gson;
import dto.Response_DTO;
import java.io.File;
import java.io.IOException;
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
        String imageType = httpServletRequest.getParameter("imageType");
        String img1 = httpServletRequest.getParameter("imageUri");

        if (httpServletRequest.getSession().getAttribute("user") == null) {
            isInvalid = true;
            message = "Please Logedin First";

        } else if (about == null || about.trim().equals("")) {
            isInvalid = true;
            message = "Missing About";

        } else if (imageType == null || imageType.trim().equals("")) {
            isInvalid = true;
            message = "Missing File Type";

        } else if (img1 == null || img1.trim().equals("")) {
            isInvalid = true;
            message = "Missing File";

        } else {

            if (!imageType.equals("image")) {
                isInvalid = true;
                message = "Invalid Type";

            } else if (about.length() > 45) {
                isInvalid = true;
                message = "About Too Long";

            } else {

                String allowedExtentions[] = {".png", ".jpg", ".jpeg"};
                File file = new File(img1);

                isInvalid = true;
                message = "Invalid Extention";

                for (String allowedExtention : allowedExtentions) {
                    if (img1.endsWith(allowedExtention)) {
                        isInvalid = false;
                        message = "";
                        break;
                    }
                }

                if (!isInvalid) {

                    if (!file.exists()) {
                        isInvalid = true;
                        message = "Image Do Not Exists";

                    } else {

                        request.setAttribute("image", img1);
                        request.setAttribute("about", about);
                        request.setAttribute("fileType", imageType);

                        chain.doFilter(request, response);

                    }
                }

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
