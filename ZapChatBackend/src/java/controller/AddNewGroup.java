package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entity.Group_chat;
import entity.Group_member;
import entity.Group_member_role;
import entity.Group_message;
import entity.Group_table;
import entity.Message_content_type;
import entity.Message_status;
import entity.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "AddNewGroup", urlPatterns = {"/AddNewGroup"})
public class AddNewGroup extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        boolean isSuccess = true;
        String message = "";
        
        String groupName = (String) request.getAttribute("groupName");
        
        String extention = (String) request.getAttribute("extention");
        Part image = (Part) request.getAttribute("image");
        
        User user = (User) request.getSession().getAttribute("user");
        
        String applicationPath = request.getServletContext().getRealPath("");
        String newApplicationPath = applicationPath.replace("build" + File.separator + "web", "web");
        
        File folder = new File(newApplicationPath + File.separator + "group-images");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        
        File file = new File(folder, user.getId() + extention);
        try (InputStream inputStream = image.getInputStream()) {
            Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
            message = "Image Upload Failed";
        }
        
        Gson gson = new Gson();
        Response_DTO response_DTO;
        if (isSuccess) {
            String imgPath = "/group-images/" + user.getId() + extention;
            user.setProfile_image(imgPath);

//            message = imgPath;
            Session openSession = HibernateUtil.getSessionFactory().openSession();
            
            Group_table newGroup = new Group_table();
            newGroup.setImage_path(imgPath);
            newGroup.setName(groupName);
            
            int groupId = (int) openSession.save(newGroup);
            openSession.beginTransaction().commit();
            Group_table g = (Group_table) openSession.get(Group_table.class, groupId);
            
            Criteria createCriteria = openSession.createCriteria(Group_member_role.class);
            createCriteria.add(Restrictions.eq("role", "Admin"));
            Group_member_role role = (Group_member_role) createCriteria.uniqueResult();
            
            Group_member newMemebr = new Group_member();
            newMemebr.setUser(user);
            newMemebr.setGroup_member_role(role);
            newMemebr.setGroup_table(g);
            
            int groupMemberId = (int) openSession.save(newMemebr);
            openSession.beginTransaction().commit();
            Group_member gMember = (Group_member) openSession.get(Group_member.class, groupMemberId);
            
            Criteria contentTypeCriteria = openSession.createCriteria(Message_content_type.class);
            contentTypeCriteria.add(Restrictions.eq("type", "Message"));
            Message_content_type type = (Message_content_type) contentTypeCriteria.uniqueResult();
            
            Criteria statusCriteria = openSession.createCriteria(Message_status.class);
            statusCriteria.add(Restrictions.eq("status", "Send"));
            Message_status status = (Message_status) statusCriteria.uniqueResult();
            
            Group_chat gh = new Group_chat();
            gh.setDatetime(new Date());
            gh.setGroup_member(gMember);
            gh.setGroup_table(g);
            gh.setMessage_content_type(type);
            gh.setMessage_status(status);
            
            int groupChatId = (int) openSession.save(gh);
            openSession.beginTransaction().commit();
            Group_chat chat = (Group_chat) openSession.get(Group_chat.class, groupChatId);
            
            Group_message gm = new Group_message();
            gm.setGroup_chat(chat);
            gm.setMessage("Welcome to " + groupName);
            
            openSession.save(gm);
            openSession.beginTransaction().commit();
            
            openSession.close();
            
            JsonObject jo = new JsonObject();
            jo.addProperty("groupId", groupId);
            jo.addProperty("image", imgPath);
            jo.addProperty("name", groupName);
            jo.addProperty("members", 1);
            jo.addProperty("isNew", false);
//            message=String.valueOf(groupId);
//            request.setAttribute("groupId", groupId);
//            request.getRequestDispatcher("/SingleGroup").include(request, response);
            response_DTO = new Response_DTO(isSuccess, gson.toJsonTree(jo));
            
        } else {
            response_DTO = new Response_DTO(isSuccess, message);
        }
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
    }
    
}
