package model.socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entity.Status;
import entity.User;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class SocketOperations {

    public JsonObject loadHome(JsonObject object) {

        FilterOperations filter = new FilterOperations();
        JsonObject LoadHomeFilter = filter.LoadHomeFilter(object);

        if (LoadHomeFilter.get("isSuccess").getAsBoolean()) {

            ServletOperations servletOperations = new ServletOperations();
            JsonObject LoadHomeObject = servletOperations.LoadHome(object);
            return LoadHomeObject;
        } else {
            return LoadHomeFilter;
        }

    }

    public JsonObject sendGroupChat(JsonObject object) {

        FilterOperations filter = new FilterOperations();
        JsonObject SendGroupMessageFilter = filter.SendGroupMessageFilter(object);

        if (SendGroupMessageFilter.get("isSuccess").getAsBoolean()) {

            ServletOperations servletOperations = new ServletOperations();
            JsonObject SendGroupMessage = servletOperations.SendGroupMessage(object);

            return SendGroupMessage;
        } else {
            return SendGroupMessageFilter;
        }

    }

    public JsonObject saveChat(JsonObject object) {

        FilterOperations filter = new FilterOperations();
        JsonObject SendMessageFilter = filter.SendMessageFilter(object);

        if (SendMessageFilter.get("isSuccess").getAsBoolean()) {

            ServletOperations servletOperations = new ServletOperations();
            JsonObject SendMessageObject = servletOperations.SendMessage(object);

            return SendMessageObject;
        } else {
            return SendMessageFilter;
        }

    }

    public JsonObject login(JsonObject object) {

        FilterOperations filter = new FilterOperations();
        JsonObject loginFilter = filter.LoginFilter(object);

        if (loginFilter.get("isSuccess").getAsBoolean()) {

            ServletOperations servletOperations = new ServletOperations();
            JsonObject LoginObject = servletOperations.Login(object);

            return LoginObject;
        } else {
            return loginFilter;
        }

    }

    public void updateChatAsSeen(JsonObject object) {

//        String receiverMobile = object.get("who").getAsString();
//        String senderMobile = object.get("whose").getAsString();
//
//        User sender = new User();
//        sender.setMobile(senderMobile);
//
//        User receiver = new User();
//        receiver.setMobile(receiverMobile);
//
//        try {
//            Session session = HibernateUtil.getSessionFactory().openSession();
//            Criteria criteria = session.createCriteria(Chat.class);
//
//            Status status = new Status();
//            status.setId(3);
//
//            criteria.add(Restrictions.and(
//                    Restrictions.eq("receiver", receiver),
//                    Restrictions.eq("sender", sender),
//                    Restrictions.ne("status", status)
//            ));
//
//            List<Chat> unseenChatList = criteria.list();
//
//            for (Chat chat : unseenChatList) {
//                chat.setStatus(status);
//                session.update(chat);
//                session.beginTransaction().commit();
//            }
//
//            System.out.println("chat status updated to 3 successfully");
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }
    }

    public void updateChatAsDelivered(JsonObject object) {

//        String receiverMobile = object.get("mobile").getAsString();
//
//        User receiver = new User();
//        receiver.setMobile(receiverMobile);
//
//        try {
//            Session session = HibernateUtil.getSessionFactory().openSession();
//            Criteria criteria = session.createCriteria(Chat.class);
//
//            Status status = new Status();
//            status.setId(1);
//
//            criteria.add(Restrictions.and(
//                    Restrictions.eq("receiver", receiver),
//                    Restrictions.eq("status", status)
//            ));
//
//            status.setId(2);
//
//            List<Chat> undeliveredChatList = criteria.list();
//
//            for (Chat chat : undeliveredChatList) {
//                chat.setStatus(status);
//                session.update(chat);
//                session.beginTransaction().commit();
//            }
//
//            System.out.println("chat status updated to 2 successfully");
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }
    }

}
