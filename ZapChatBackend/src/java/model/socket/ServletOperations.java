package model.socket;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entity.File;
import entity.Group_chat;
import entity.Group_member;
import entity.Group_message;
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

        boolean isSearch = false;

        String searchText = jsonObject.get("searchText").getAsString();
        if (!searchText.trim().equals("")) {
            isSearch = true;
        }

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        int jsonuserId = jsonObject.get("userId").getAsInt();
        User user = (User) hibernateSession.get(User.class, jsonuserId);

        Criteria userCriteria = hibernateSession.createCriteria(User.class);
        userCriteria.add(Restrictions.ne("id", user.getId()));
        List<User> userList = userCriteria.list();

        ArrayList<Single_chat> chatArray = new ArrayList<>();
        ArrayList<Single_chat> searchChatArray = new ArrayList<>();

        boolean isFound = false;

        JsonArray jsonArray = new JsonArray();

        if (!userList.isEmpty()) {
            //has more users

            for (User otherUser : userList) {

                Criteria chatCriteria = hibernateSession.createCriteria(Single_chat.class);
                chatCriteria.add(Restrictions.or(
                        Restrictions.and(
                                Restrictions.eq("from_user", user),
                                Restrictions.eq("to_user", otherUser)
                        ),
                        Restrictions.and(
                                Restrictions.eq("from_user", otherUser),
                                Restrictions.eq("to_user", user)
                        )
                ));
                chatCriteria.addOrder(Order.desc("datetime"));
                chatCriteria.setMaxResults(1);
                Single_chat lastChat = (Single_chat) chatCriteria.uniqueResult();

                if (lastChat != null) {
                    //has last chat

                    chatArray.add(lastChat);
                }

            }

            if (isSearch) {

                String searchLower = searchText.toLowerCase();

                for (Single_chat single_chat : chatArray) {

                    String fromFirstName = single_chat.getFrom_user().getFirst_name().toLowerCase();
                    String fromLastName = single_chat.getFrom_user().getLast_name().toLowerCase();
                    String toFirstName = single_chat.getTo_user().getFirst_name().toLowerCase();
                    String toLastName = single_chat.getTo_user().getLast_name().toLowerCase();

                    if (fromFirstName.contains(searchLower)
                            || fromLastName.contains(searchLower)
                            || toFirstName.contains(searchLower)
                            || toLastName.contains(searchLower)) {

                        searchChatArray.add(single_chat);
                    }

                }
            }

            ArrayList<Single_chat> searchFrom;
            if (isSearch) {
                searchFrom = searchChatArray;
            } else {
                searchFrom = chatArray;
            }
            for (Single_chat single_chat : searchFrom) {

                if (!isFound) {
                    isFound = true;
                }

                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.addProperty("chatId", single_chat.getId());

                if (user.getId() == single_chat.getFrom_user().getId()) {
                    //i have send this message
                    //need to get to user details

                    jsonObject2.addProperty("userId", single_chat.getTo_user().getId());
                    jsonObject2.addProperty("name", single_chat.getTo_user().getFirst_name() + " " + single_chat.getTo_user().getLast_name());

                    if (single_chat.getTo_user().getProfile_image().equals("../assets/images/default.svg")) {
                        jsonObject2.addProperty("image", "../assets/images/person-square.svg");

                    } else {
                        jsonObject2.addProperty("image", single_chat.getTo_user().getProfile_image());
                    }

                    jsonObject2.addProperty("onlineStatus", single_chat.getTo_user().getUser_online_status().getStatus());
                    jsonObject2.addProperty("about", single_chat.getTo_user().getAbout());
                    jsonObject2.addProperty("showTick", true);

                } else {
                    //received message
                    //need to get from user details

                    jsonObject2.addProperty("userId", single_chat.getFrom_user().getId());
                    jsonObject2.addProperty("name", single_chat.getFrom_user().getFirst_name() + " " + single_chat.getFrom_user().getLast_name());

                    if (single_chat.getFrom_user().getProfile_image().equals("../assets/images/default.svg")) {
                        jsonObject2.addProperty("image", "../assets/images/person-square.svg");

                    } else {
                        jsonObject2.addProperty("image", single_chat.getTo_user().getProfile_image());
                    }

                    jsonObject2.addProperty("onlineStatus", single_chat.getFrom_user().getUser_online_status().getStatus());
                    jsonObject2.addProperty("about", single_chat.getFrom_user().getAbout());
                    jsonObject2.addProperty("showTick", false);

                }
                jsonObject2.addProperty("messageStatus", single_chat.getMessage_status().getStatus());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                SimpleDateFormat time = new SimpleDateFormat("HH:mm");
                SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");

                boolean equal = date.format(new Date()).equals(date.format(single_chat.getDatetime()));
                if (equal) {
                    //same day
                    jsonObject2.addProperty("datetime", time.format(single_chat.getDatetime()));

                } else {
                    jsonObject2.addProperty("datetime", date.format(single_chat.getDatetime()));

                }

                if (single_chat.getMessage_content_type().getType().equals("Message")) {

                    Criteria msgCriteria = hibernateSession.createCriteria(Message.class);
                    msgCriteria.add(Restrictions.eq("single_chat", single_chat));

                    Message msg = (Message) msgCriteria.uniqueResult();

                    jsonObject2.addProperty("lastMessage", msg.getMessage());

                } else {
                    jsonObject2.addProperty("lastMessage", "File Content");

                }
                jsonArray.add(jsonObject2);
            }

        }

        Gson gson = new Gson();
        JsonObject jo = new JsonObject();
        jo.addProperty("profile", user.getProfile_image());
        jo.addProperty("isFound", isFound);
        jo.addProperty("location", "home");
        jo.add("data", gson.toJsonTree(jsonArray));

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
        jo.addProperty("messageId", messageId);
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
//                request.getSession().setAttribute("user", user);

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
//            sessionId = request.getSession().getId();
//            jo.addProperty("sessionId", sessionId);

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
