package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entity.Status;
import entity.Status_item;
import entity.User;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadStatus", urlPatterns = {"/LoadStatus"})
public class LoadStatus extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        boolean isSearch = (boolean) request.getAttribute("isSearch");
        String searchText = (String) request.getAttribute("searchText");

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        Criteria statusCriteria = hibernateSession.createCriteria(Status.class);
        statusCriteria.addOrder(Order.desc("id"));
        ArrayList<Status> statusList = (ArrayList<Status>) statusCriteria.list();

        boolean isFound = false;

        /*
        
        {
            isFound:true/false,
            data:
            [ <--------------------------------- user array
                { <----------------------------- 1st user object
                    statusId:"",
                    name:"",
                    lastStatusTime(datetime):"",
                    image:"",
                    status:[ <------------------ status array
                        { <--------------------- 1st statusof this user
                            statusItemId:"",
                            image:"",
                            isImage:true/false,
                            text:"",
                            isText:true/false
                        },
                        { <--------------------- 2nd statusof this user
                            statusItemId:"",
                            image:"",
                            isImage:true/false,
                            text:"",
                            isText:true/false
                        }
                    ]
                },
            ]
        }

         */
        JsonArray userArray = new JsonArray();

        if (!statusList.isEmpty()) {
            //has status

            isFound = true;

            //search
            ArrayList<Status> searchStatusArray = new ArrayList<>();
            if (isSearch) {
                String searchLower = searchText.toLowerCase();

                for (Status status : statusList) {

                    String firstName = status.getUser().getFirst_name();
                    String lastName = status.getUser().getLast_name();

                    if (firstName.contains(searchLower) || lastName.contains(searchLower)) {
                        searchStatusArray.add(status);
                    }

                }
            }

            ArrayList<Status> searchFrom;
            if (isSearch) {
                searchFrom = searchStatusArray;
            } else {
                searchFrom = statusList;
            }

            //load data
            for (Status status : searchFrom) {
                User statusUser = status.getUser();

                JsonObject userObject = new JsonObject();
                userObject.addProperty("statusId", status.getId());
                userObject.addProperty("name", statusUser.getFirst_name() + " " + statusUser.getLast_name());
                userObject.addProperty("image", statusUser.getProfile_image());

                //status items
                Criteria statusItemCriteria = hibernateSession.createCriteria(Status_item.class);
                statusItemCriteria.add(Restrictions.eq("status", status));
                statusItemCriteria.addOrder(Order.asc("datetime"));
                List<Status_item> statusItemList = statusItemCriteria.list();


                int size = statusItemList.size();
                int i = 0;
                
                JsonArray statusArray = new JsonArray();
                for (Status_item statusItem : statusItemList) {
                    i++;
                    if (size == i) {

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
                        SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");

                        boolean equal = date.format(new Date()).equals(date.format(statusItem.getDatetime()));
                        if (equal) {
                            //same day
                            userObject.addProperty("datetime", time.format(statusItem.getDatetime()));

                        } else {
                            userObject.addProperty("datetime", date.format(statusItem.getDatetime()));

                        }

                    }

                    JsonObject statusObject = new JsonObject();
                    statusObject.addProperty("statusItemId", statusItem.getId());

                    boolean isText = false;
                    boolean isImage = false;

                    if (statusItem.getText() != null && !statusItem.getText().trim().equals("")) {

                        isText = true;
                        statusObject.addProperty("text", statusItem.getText());
                    }

                    if (statusItem.getFile_path() != null && !statusItem.getFile_path().trim().equals("")) {

                        isImage = true;
                        statusObject.addProperty("image", statusItem.getFile_path());
                    }

                    statusObject.addProperty("isText", isText);
                    statusObject.addProperty("isImage", isImage);

                    statusArray.add(statusObject);

                }
                userObject.add("status", statusArray);
                userArray.add(userObject);

            }
        }

        Gson gson = new Gson();

        JsonObject jo = new JsonObject();
        jo.addProperty("isFound", isFound);
        jo.add("data", gson.toJsonTree(userArray));

        Response_DTO response_DTO = new Response_DTO(true, gson.toJsonTree(jo));

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));

    }

}
