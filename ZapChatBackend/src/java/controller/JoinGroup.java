package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entity.Group_member;
import entity.Group_member_role;
import entity.Group_table;
import entity.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "JoinGroup", urlPatterns = {"/JoinGroup"})
public class JoinGroup extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        int groupId = (int) request.getAttribute("groupId");
        User user = gson.fromJson((JsonObject) request.getAttribute("user"), User.class);

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        boolean isJoined = false;
        String message = "";

        Group_table group = (Group_table) hibernateSession.get(Group_table.class, groupId);
        if (group != null) {
            //has group

            Criteria memberCriteria = hibernateSession.createCriteria(Group_member.class);
            memberCriteria.add(Restrictions.and(
                    Restrictions.eq("user", user),
                    Restrictions.eq("group_table", group)
            ));
            Group_member member = (Group_member) memberCriteria.uniqueResult();

            if (member == null) {
                //not a member
                Criteria memberRoleCriteria = hibernateSession.createCriteria(Group_member_role.class);
                memberRoleCriteria.add(Restrictions.eq("role", "Member"));
                Group_member_role memberRole = (Group_member_role) memberRoleCriteria.uniqueResult();

                Group_member newMember = new Group_member();
                newMember.setGroup_member_role(memberRole);
                newMember.setGroup_table(group);
                newMember.setUser(user);

                hibernateSession.save(newMember);
                hibernateSession.beginTransaction().commit();

                isJoined = true;
                message = "Joined Successfully";
            } else {
                message = "Already a Member";
            }

        } else {
            message = "Cannot Find the Group";
        }

        hibernateSession.close();

        Response_DTO response_DTO = new Response_DTO(isJoined, message);

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));

    }

}
