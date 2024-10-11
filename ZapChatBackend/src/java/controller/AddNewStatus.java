package controller;

import com.google.gson.Gson;
import dto.Response_DTO;
import entity.Status;
import entity.Status_item;
import entity.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "AddNewStatus", urlPatterns = {"/AddNewStatus"})
public class AddNewStatus extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        boolean isSuccess = true;
        String message = "";
        Gson gson = new Gson();

        boolean isImage = (boolean) request.getAttribute("isImage");
        boolean isText = (boolean) request.getAttribute("isText");

        String extention = "";
        Part image = null;

        if (isImage) {
            extention = (String) request.getAttribute("extention");
            image = (Part) request.getAttribute("image");

            if (image == null || image.getSize() == 0) {
                message = "No image file uploaded";
                isSuccess = false;
            }
        }

        String text = "";
        if (isText) {

            text = (String) request.getAttribute("text");
        }

        if (isSuccess) {

            User user = gson.fromJson((String) request.getAttribute("user"), User.class);

            Session openSession = HibernateUtil.getSessionFactory().openSession();

            boolean isImageSuccess = false;
            String imgPath = "";

            if (isImage) {

                String applicationPath = request.getServletContext().getRealPath("");
                String newApplicationPath = applicationPath.replace("build" + File.separator + "web", "web");

                File folder = new File(newApplicationPath + File.separator + "status-images");
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                String fname = System.currentTimeMillis() + extention;

                File file = new File(folder, fname);
                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    isSuccess = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                isImageSuccess = true;
                imgPath = "/status-images/" + fname;

            }

            if ((isImage && isImageSuccess) || isText) {

                Criteria statusCriteria = openSession.createCriteria(Status.class);
                statusCriteria.add(Restrictions.eq("user", user));
                Status avaliableStatus = (Status) statusCriteria.uniqueResult();

                Status status = null;

                if (avaliableStatus == null) {
                    Status newStatus = new Status();
                    newStatus.setUser(user);

                    int statusId = (int) openSession.save(newStatus);
                    status = (Status) openSession.get(Status.class, statusId);
                } else {
                    status = avaliableStatus;
                }

                Status_item newStatusItem = new Status_item();
                newStatusItem.setDatetime(new Date());
                newStatusItem.setStatus(status);

                if (isImage && isImageSuccess) {
                    newStatusItem.setFile_path(imgPath);
                }
                if (isText) {
                    newStatusItem.setText(text);
                }

                openSession.save(newStatusItem);
            }
            openSession.beginTransaction().commit();
            openSession.close();

        }

        Response_DTO response_DTO = new Response_DTO(isSuccess, message);
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));

    }

}
