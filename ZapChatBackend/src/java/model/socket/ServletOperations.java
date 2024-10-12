package model.socket;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.File;
import entity.Group_chat;
import entity.Group_file;
import entity.Group_member;
import entity.Group_message;
import entity.Group_table;
import entity.Message;
import entity.Message_content_type;
import entity.Message_status;
import entity.Single_chat;
import entity.Status;
import entity.Status_item;
import entity.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class ServletOperations {

    public JsonObject LoadLastStatus(JsonObject jsonObject) {

        Session openSession = HibernateUtil.getSessionFactory().openSession();
        JsonObject jsonuser = jsonObject.get("user").getAsJsonObject();
        User user = (User) openSession.get(User.class, jsonuser.get("id").getAsInt());

        System.out.println("userId: "+user.getId());
        System.out.println("userId: "+user.getId());
        System.out.println("userId: "+user.getId());
        System.out.println("userId: "+user.getId());
        System.out.println("userId: "+user.getId());
        System.out.println("userId: "+user.getId());
        System.out.println("userId: "+user.getId());
        
        Criteria statusCriteria = openSession.createCriteria(Status.class);
        statusCriteria.add(Restrictions.eq("user", user));
        statusCriteria.addOrder(Order.desc("id"));
        statusCriteria.setMaxResults(1);
        Status status = (Status) statusCriteria.uniqueResult();

        boolean isSuccess = true;

        JsonArray ja = new JsonArray();
        JsonObject userObject = new JsonObject();

        if (status != null) {

            Criteria statusItemCriteria = openSession.createCriteria(Status_item.class);
            statusItemCriteria.add(Restrictions.eq("status", status));
            statusItemCriteria.addOrder(Order.asc("datetime"));
            List<Status_item> list = statusItemCriteria.list();

            if (!list.isEmpty()) {

                userObject.addProperty("statusId", status.getId());
                userObject.addProperty("name", user.getFirst_name() + " " + user.getLast_name());
                userObject.addProperty("image", user.getProfile_image());

                JsonArray statusArray = new JsonArray();

                int size = list.size();
                int i = 0;
                for (Status_item statusItem : list) {
                    i++;
                    if (size==i) {

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
            } else {
                isSuccess = false;
            }

        } else {
            isSuccess = false;
        }
        userObject.addProperty("location", "status");

        JsonObject jo = new JsonObject();
        jo.addProperty("success", isSuccess);
        jo.addProperty("location", "status");
        jo.add("data", userObject);

        return jo;
    }

    public JsonObject SendGroupMessage(JsonObject jsonObject) {

        int groupId = jsonObject.get("groupId").getAsInt();
        String contentType = jsonObject.get("contentType").getAsString();
        String content = jsonObject.get("content").getAsString();

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        Group_table group = (Group_table) hibernateSession.get(Group_table.class, groupId);

        JsonObject jsonuser = jsonObject.get("user").getAsJsonObject();
        User user = (User) hibernateSession.get(User.class, jsonuser.get("id").getAsInt());

        Criteria groupMemberCriteria = hibernateSession.createCriteria(Group_member.class);
        groupMemberCriteria.add(Restrictions.and(
                Restrictions.eq("user", user),
                Restrictions.eq("group_table", group)
        ));
        Group_member group_member = (Group_member) groupMemberCriteria.uniqueResult();

        Criteria messageStatusCriteria = hibernateSession.createCriteria(Message_status.class);
        messageStatusCriteria.add(Restrictions.eq("status", "Send"));
        Message_status sendStatus = (Message_status) messageStatusCriteria.uniqueResult();

        Criteria messageContentTypeCriteria = hibernateSession.createCriteria(Message_content_type.class);
        messageContentTypeCriteria.add(Restrictions.eq("type", contentType));
        Message_content_type contentTypeResult = (Message_content_type) messageContentTypeCriteria.uniqueResult();

        SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm");

        Date newDate = new Date();

        // new group chat
        Group_chat groupChat = new Group_chat();
        groupChat.setGroup_member(group_member);
        groupChat.setDatetime(newDate);
        groupChat.setMessage_status(sendStatus);
        groupChat.setMessage_content_type(contentTypeResult);
        groupChat.setGroup_table(group);

        int groupChatId = (int) hibernateSession.save(groupChat);

        //if content type is message -> add to message
        //if content type is file -> add to file
        Group_chat savedGroupChat = (Group_chat) hibernateSession.get(Group_chat.class, groupChatId);
        int messageId = 0;
        int fileId = 0;
        if (contentType.equals("Message")) {

            Group_message message = new Group_message();
            message.setMessage(content);
            message.setGroup_chat(savedGroupChat);

            messageId = (int) hibernateSession.save(message);

        } else if (contentType.equals("File")) {

            Group_file file = new Group_file();
            file.setPath(content);
            file.setGroup_chat(savedGroupChat);

            fileId = (int) hibernateSession.save(file);

        }

        String side = "right";

        hibernateSession.beginTransaction().commit();
        hibernateSession.close();

        JsonObject jo = new JsonObject();
        jo.addProperty("type", contentType);
        if (contentType.equals("Message")) {
            jo.addProperty("messageId", messageId);
            jo.addProperty("message", content);
        } else {
            jo.addProperty("fileId", fileId);
            jo.addProperty("path", content);
        }
        jo.addProperty("time", timeSdf.format(newDate));
        jo.addProperty("date", dateSdf.format(newDate));
        jo.addProperty("messageStatus", sendStatus.getStatus());
        jo.addProperty("isSuccess", true);
        jo.addProperty("location", "send_group_chat");
        jo.addProperty("side", side);
        jo.addProperty("senderName", user.getFirst_name() + "...");
        jo.addProperty("senderImg", user.getProfile_image());
        jo.addProperty("groupId", groupId);

        return jo;

    }

    public JsonObject LoadHome(JsonObject jsonObject) {

        String category = jsonObject.get("category").getAsString();

        JsonObject jo = null;
        switch (category) {
            case "chat":
                jo = LoadChat(jsonObject);
                break;
            case "group":
                jo = LoadGroup(jsonObject);
                break;
            case "status":
                jo = LoadStatus(jsonObject);
                break;
            default:
                break;
        }

        return jo;

    }

    public JsonObject LoadChat(JsonObject jsonObject) {

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        int fromjsonuserId = jsonObject.get("userId").getAsInt();
        User fromuser = (User) hibernateSession.get(User.class, fromjsonuserId);//from

        int tojsonuserId = jsonObject.get("otherUserId").getAsInt();
        User touser = (User) hibernateSession.get(User.class, tojsonuserId);//from

        Criteria chatCriteria = hibernateSession.createCriteria(Single_chat.class);
        chatCriteria.add(Restrictions.and(
                Restrictions.eq("from_user", fromuser),
                Restrictions.eq("to_user", touser)
        ));
        chatCriteria.addOrder(Order.desc("datetime"));
        chatCriteria.setMaxResults(1);
        Single_chat lastChat = (Single_chat) chatCriteria.uniqueResult();

        //    {
        //        "chatId": 81,
        //        "userId": 1,
        //        "name": "Pasindu  Jayasundara ",
        //        "image": "/profile-images/1.jpeg",
        //        "onlineStatus": "Online",
        //        "about": "Hi, how are you? Boy ",
        //        "showTick": true,
        //        "messageStatus": "Send",
        //        "datetime": "02:02",
        //        "lastMessage": "Hxbzbxjd"
        //    }
        JsonObject sendjo = new JsonObject();
        boolean isFound = true;
        
        if (lastChat != null) {
            sendjo.addProperty("chatId", lastChat.getId());
            sendjo.addProperty("messageStatus", lastChat.getMessage_status().getStatus());

            SimpleDateFormat time = new SimpleDateFormat("HH:mm");
            SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");

            boolean equal = date.format(new Date()).equals(date.format(lastChat.getDatetime()));
            if (equal) {
                //same day
                sendjo.addProperty("datetime", time.format(lastChat.getDatetime()));

            } else {
                sendjo.addProperty("datetime", date.format(lastChat.getDatetime()));

            }

            if (lastChat.getMessage_content_type().getType().equals("Message")) {

                Criteria msgCriteria = hibernateSession.createCriteria(Message.class);
                msgCriteria.add(Restrictions.eq("single_chat", lastChat));

                Message msg = (Message) msgCriteria.uniqueResult();

                sendjo.addProperty("lastMessage", msg.getMessage());

            } else {
                sendjo.addProperty("lastMessage", "File Content");
            }
        } else {
            isFound=false;
            
            sendjo.addProperty("chatId", System.currentTimeMillis());
            sendjo.addProperty("messageStatus", "");
            sendjo.addProperty("datetime", "");
            sendjo.addProperty("lastMessage", "");

        }
        Gson gson = new Gson();
        JsonObject jo = new JsonObject();
        jo.addProperty("isFound", isFound);
        jo.addProperty("location", "home");
        jo.add("data", gson.toJsonTree(sendjo));

        return jo;

    }

    public JsonObject LoadGroup(JsonObject jsonObject) {

        Gson gson = new Gson();
        boolean isSearch = false;

        String searchText = jsonObject.get("searchText").getAsString();
        if (!searchText.trim().equals("")) {
            isSearch = true;
        }

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        JsonObject jsonuser = jsonObject.get("user").getAsJsonObject();
        User user = (User) hibernateSession.get(User.class, jsonuser.get("id").getAsInt());

        Criteria groupMemberCriteria = hibernateSession.createCriteria(Group_member.class);
        groupMemberCriteria.add(Restrictions.eq("user", user));
        groupMemberCriteria.addOrder(Order.desc("id"));

        List<Group_member> groupMemberList = groupMemberCriteria.list();

        ArrayList<Group_member> memberArray = new ArrayList<>();
        ArrayList<Group_member> searchGroupArray = new ArrayList<>();

        boolean isFound = false;

        JsonArray jsonArray = new JsonArray();

        if (!groupMemberList.isEmpty()) {
            // member of a group

            for (Group_member groupMember : groupMemberList) {
                memberArray.add(groupMember);
            }

            if (isSearch) {
                String searchLower = searchText.toLowerCase();

                for (Group_member groupMember : memberArray) {
                    String groupName = groupMember.getGroup_table().getName().toLowerCase();

                    if (groupName.contains(searchLower)) {
                        searchGroupArray.add(groupMember);
                    }

                }
            }

            ArrayList<Group_member> searchFrom;
            if (isSearch) {
                searchFrom = searchGroupArray;
            } else {
                searchFrom = memberArray;
            }
            for (Group_member groupMember : searchFrom) {

                Criteria groupChatCriteria = hibernateSession.createCriteria(Group_chat.class);
                groupChatCriteria.add(Restrictions.eq("group_member", groupMember));
                groupChatCriteria.addOrder(Order.desc("id"));
                groupChatCriteria.setMaxResults(1);
                Group_chat groupChat = (Group_chat) groupChatCriteria.uniqueResult();

                if (groupChat != null) {

                    if (!isFound) {
                        isFound = true;
                    }

                    JsonObject jsonObject2 = new JsonObject();
                    jsonObject2.addProperty("groupId", groupMember.getGroup_table().getId());
                    jsonObject2.addProperty("name", groupMember.getGroup_table().getName());
                    jsonObject2.addProperty("image", groupMember.getGroup_table().getImage_path());

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    SimpleDateFormat time = new SimpleDateFormat("HH:mm");
                    SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");

                    boolean equal = date.format(new Date()).equals(date.format(groupChat.getDatetime()));
                    if (equal) {
                        //same day
                        jsonObject2.addProperty("datetime", time.format(groupChat.getDatetime()));

                    } else {
                        jsonObject2.addProperty("datetime", date.format(groupChat.getDatetime()));

                    }

                    if (groupChat.getMessage_content_type().getType().equals("Message")) {

                        Criteria msgCriteria = hibernateSession.createCriteria(Group_message.class);
                        msgCriteria.add(Restrictions.eq("group_chat", groupChat));

                        Group_message msg = (Group_message) msgCriteria.uniqueResult();

                        jsonObject2.addProperty("lastMessage", msg.getMessage());

                    } else {
                        jsonObject2.addProperty("lastMessage", "File Content");
                    }

                    Criteria memberCountCriteria = hibernateSession.createCriteria(Group_member.class);
                    memberCountCriteria.add(Restrictions.eq("group_table", groupMember.getGroup_table()));
                    memberCountCriteria.setProjection(Projections.count("id"));
                    Long count = (Long) memberCountCriteria.uniqueResult();

                    jsonObject2.addProperty("members", count);

                    Criteria isNewCriteria = hibernateSession.createCriteria(Group_member.class);
                    isNewCriteria.add(Restrictions.and(
                            Restrictions.eq("user", user),
                            Restrictions.eq("group_table", groupMember.getGroup_table())
                    ));
                    Group_member isNew = (Group_member) isNewCriteria.uniqueResult();

                    if (isNew != null) {
                        jsonObject2.addProperty("isNew", false);
                    } else {
                        jsonObject2.addProperty("isNew", true);
                    }

                    jsonArray.add(jsonObject2);
                }
            }

        }

        JsonObject jo = new JsonObject();
        jo.addProperty("isFound", isFound);
        jo.addProperty("location", "group");

        jo.add("data", gson.toJsonTree(jsonArray));

        return jo;
    }

    public JsonObject LoadStatus(JsonObject jsonObject) {

        boolean isSearch = false;

        String searchText = jsonObject.get("searchText").getAsString();
        if (!searchText.trim().equals("")) {
            isSearch = true;
        }

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
                statusItemCriteria.addOrder(Order.desc("datetime"));
                List<Status_item> statusItemList = statusItemCriteria.list();

                boolean isFirstTime = true;

                JsonArray statusArray = new JsonArray();
                for (Status_item statusItem : statusItemList) {

                    if (isFirstTime) {

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

                        isFirstTime = false;
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
        jo.addProperty("location", "status");

        jo.add("data", gson.toJsonTree(userArray));

        return jo;

    }

    public JsonObject SendMessage(JsonObject jsonObject) {

        int otherUserId = jsonObject.get("otherUserId").getAsInt();
        int fromUserId = jsonObject.get("fromUserId").getAsInt();
        String contentType = jsonObject.get("contentType").getAsString();
        String content = jsonObject.get("content").getAsString();

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        User otherUser = (User) hibernateSession.get(User.class, otherUserId);
        User fromUser = (User) hibernateSession.get(User.class, fromUserId);

        Criteria messageStatusCriteria = hibernateSession.createCriteria(Message_status.class);
        messageStatusCriteria.add(Restrictions.eq("status", "Send"));
        Message_status sendStatus = (Message_status) messageStatusCriteria.uniqueResult();

        Criteria messageContentTypeCriteria = hibernateSession.createCriteria(Message_content_type.class);
        messageContentTypeCriteria.add(Restrictions.eq("type", contentType));
        Message_content_type contentTypeResult = (Message_content_type) messageContentTypeCriteria.uniqueResult();

        SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm");
        Date msgDate = new Date();

        // new single chat
        Single_chat single_chat = new Single_chat();
        single_chat.setFrom_user(fromUser);
        single_chat.setTo_user(otherUser);
        single_chat.setDatetime(msgDate);
        single_chat.setMessage_status(sendStatus);
        single_chat.setMessage_content_type(contentTypeResult);

        int singleChatId = (int) hibernateSession.save(single_chat);

        //if content type is message -> add to message
        //if content type is file -> add to file
        Single_chat savedSingleChat = (Single_chat) hibernateSession.get(Single_chat.class, singleChatId);
        int messageId = 0;
        int fileId = 0;
        if (contentType.equals("Message")) {

            Message message = new Message();
            message.setMessage(content);
            message.setSingle_chat(savedSingleChat);

            messageId = (int) hibernateSession.save(message);

        } else if (contentType.equals("File")) {

            File file = new File();
            file.setPath(content);
            file.setSingle_chat(savedSingleChat);

            fileId = (int) hibernateSession.save(file);

        }

        hibernateSession.beginTransaction().commit();
        hibernateSession.close();

        JsonObject jo = new JsonObject();
        jo.addProperty("type", contentType);
        jo.addProperty("chatId", messageId);
        jo.addProperty("message", content);
        jo.addProperty("fileId", fileId);
        jo.addProperty("path", content);
        jo.addProperty("time", timeSdf.format(msgDate));
        jo.addProperty("date", dateSdf.format(msgDate));
        jo.addProperty("messageStatus", sendStatus.getStatus());
        jo.addProperty("isSuccess", true);
        jo.addProperty("location", "send_chat");

        return jo;

    }

    public JsonObject Login(JsonObject jsonObject) {

        String mobile = jsonObject.get("mobile").getAsString();
        String password = jsonObject.get("password").getAsString();

        String message = "";
        boolean isSuccess = false;

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        Criteria userCriteria = hibernateSession.createCriteria(User.class);
        userCriteria.add(Restrictions.and(
                Restrictions.eq("mobile", mobile),
                Restrictions.eq("password", password))
        );
        User user = (User) userCriteria.uniqueResult();

        if (user != null) {
            if (user.getUser_verified_status().getStatus().equals("Not-Verified")) {
                //not verified
                message = "Not Verified";

            } else {
                //verified

                message = "Login Success";
                isSuccess = true;
            }
        } else {
            message = "Invalid Details";
        }

        String sessionId = "";
        JsonObject jo = new JsonObject();
        Gson gson = new Gson();

        if (isSuccess) {

            jo.add("user", gson.toJsonTree(user));

            jo.addProperty("profileImage", user.getProfile_image());
            jo.addProperty("profileAbout", user.getAbout());
        } else {
            jo.addProperty("msg", message);
        }
        jo.addProperty("success", isSuccess);
        jo.addProperty("location", "login");

        hibernateSession.close();

        return jo;

    }
}
