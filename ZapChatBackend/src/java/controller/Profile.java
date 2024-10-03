package controller;

import com.google.gson.Gson;
import dto.Response_DTO;
import entitiy.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.HibernateUtil;
import org.hibernate.Session;

@MultipartConfig
@WebServlet(name = "Profile", urlPatterns = {"/Profile"})
public class Profile extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        boolean isSuccess = false;
        String message = "";

        String fileType = (String) request.getAttribute("fileType");
        String about = (String) request.getAttribute("about");
        Part image = (Part) request.getAttribute("image");

        if (image == null || image.getSize() == 0) {
            message = "No image file uploaded";
            return;
        }

        String applicationPath = request.getServletContext().getRealPath("");
        String newApplicationPath = applicationPath.replace("build" + File.separator + "web", "web");

        File folder = new File(newApplicationPath + File.separator + "product-images" + File.separator + System.currentTimeMillis());
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, "uploaded_image." + fileType);
        try (InputStream inputStream = image.getInputStream()) {
            Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isSuccess) {

            String imgPath = file.getAbsolutePath();
            System.out.println(imgPath);

            User user = (User) request.getSession().getAttribute("user");
            user.setAbout(about);
            user.setProfile_image(imgPath);

            Session openSession = HibernateUtil.getSessionFactory().openSession();
            openSession.update(user);

            openSession.beginTransaction().commit();
            openSession.close();
        }

        message = "Profile Update Success";

        Response_DTO response_DTO = new Response_DTO(isSuccess, message);
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));

    }
}
