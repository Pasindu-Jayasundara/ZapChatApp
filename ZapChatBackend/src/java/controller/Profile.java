package controller;

import com.google.gson.Gson;
import dto.Response_DTO;
import entity.User;
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

        boolean isSuccess = true;
        String message = "";

        String about = (String) request.getAttribute("about");
        boolean isNewImage = (boolean) request.getAttribute("isNewImage");

        String extention = "";
        Part image = null;

        if (isNewImage) {
            extention = (String) request.getAttribute("extention");
            image = (Part) request.getAttribute("image");

            if (image == null || image.getSize() == 0) {
                message = "No image file uploaded";
                isSuccess = false;
            }
        }

        if (isSuccess) {
            User user = (User) request.getSession().getAttribute("user");

            if (isNewImage) {
                
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaa");

                String applicationPath = request.getServletContext().getRealPath("");
                String newApplicationPath = applicationPath.replace("build" + File.separator + "web", "web");

                File folder = new File(newApplicationPath + File.separator + "profile-images");
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                File file = new File(folder, user.getId() + extention);
                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    isSuccess = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String imgPath = "/profile-images/" + user.getId() + extention;
                user.setProfile_image(imgPath);

                message = imgPath;
            }

            user.setAbout(about);

            Session openSession = HibernateUtil.getSessionFactory().openSession();
            openSession.update(user);

            openSession.beginTransaction().commit();
            openSession.close();

        }

        Response_DTO response_DTO = new Response_DTO(isSuccess, message);
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));

    }
}
