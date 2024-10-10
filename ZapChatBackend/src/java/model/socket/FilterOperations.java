package model.socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import javax.servlet.http.HttpServletRequest;
import model.Validation;

public class FilterOperations {
    
    public JsonObject SendGroupMessageFilter(JsonObject jsonObject){
        
//        Gson gson = new Gson();
//        JsonObject fromJson = gson.fromJson(request.getReader(), JsonObject.class);

//        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        boolean isSuccess = false;
        String message = "";

        if (jsonObject.has("user")) {

            if (!jsonObject.has("groupId")) {
                message = "Missing Id";

            } else if (!jsonObject.has("contentType")) {
                message = "Missing Content Type";

            } else if (!jsonObject.has("content")) {
                message = "Missing Content";

            } else {

                String groupId = jsonObject.get("groupId").getAsString();
                String contentType = jsonObject.get("contentType").getAsString();
                String content = jsonObject.get("content").getAsString();
//                String user = jsonObject.get("user").getAsString();

                if (!Validation.isInteger(groupId)) {
                    message = "Invalid Id Type";

                } else if (!Validation.isValidName(contentType)) {
                    message = "Invalid Content Type Type";

                } else if (!contentType.equals("Message") && !contentType.equals("File")) {
                    message = "Invalid Content Type";

                } else {

                    boolean isOk = false;
                    if (contentType.equals("Message")) {
                        isOk = true;
                    } else if (contentType.equals("File")) {
                        isOk = true;
                    }

                    if (isOk) {

                        int id = Integer.parseInt(groupId);
                        if (id < 1) {
                            message = "Invalid Id Range";
                        } else {

                            isSuccess = true;
                        }

                    } else {
                        message = "Invalid Content";
                    }

                }
            }

        } else {
            message = "Please LogIn";
        }

        JsonObject returnObject = new JsonObject();
        returnObject.addProperty("isSuccess", isSuccess);
        returnObject.addProperty("message", message);
        returnObject.addProperty("location", "send_group_chat");
        
        return returnObject;
        
    }

    public JsonObject LoadHomeFilter(JsonObject jsonObject) {

//        boolean isSearch = false;
        boolean isInvalid = false;
        String message = "";

//        Gson gson = new Gson();
        if (jsonObject.has("userId")) {

            if (!jsonObject.has("searchText")) {
                isInvalid = true;
                message = "Missing Search Text";

            } else if (!jsonObject.has("category")) {
                isInvalid = true;
                message = "Missing Category";

            } else {

                String category = jsonObject.get("category").getAsString();

                if (category.trim().equals("")) {
                    isInvalid = true;
                    message = "Empty Category";

                } else {

                    isInvalid = false;

                }

            }

        } else {
            isInvalid = true;
            message = "Please LogIn";
        }

        JsonObject returnObject = new JsonObject();
        returnObject.addProperty("isSuccess", !isInvalid);
        returnObject.addProperty("message", message);
        returnObject.addProperty("location", "home");
        
        return returnObject;

    }

    public JsonObject SendMessageFilter(JsonObject jsonObject) {

        boolean isSuccess = false;
        String message = "";

        if (jsonObject.get("fromUserId") != null) {

            if (!jsonObject.has("otherUserId")) {
                message = "Missing Id";

            } else if (!jsonObject.has("contentType")) {
                message = "Missing Content Type";

            } else if (!jsonObject.has("content")) {
                message = "Missing Content";

            } else {

                String otherUserId = jsonObject.get("otherUserId").getAsString();
                String contentType = jsonObject.get("contentType").getAsString();

                if (!Validation.isInteger(otherUserId)) {
                    message = "Invalid Id Type";

                } else if (!Validation.isValidName(contentType)) {
                    message = "Invalid Content Type Type";

                } else if (!contentType.equals("Message") && !contentType.equals("File")) {
                    message = "Invalid Contnt Type";

                } else {

                    boolean isOk = false;
                    if (contentType.equals("Message")) {
                        isOk = true;
                    } else if (contentType.equals("File")) {
                        isOk = true;
                    }

                    if (isOk) {

                        int id = Integer.parseInt(otherUserId);
                        if (id < 1) {
                            message = "Invalid Id Range";
                        } else {

                            isSuccess = true;

                        }

                    } else {
                        message = "Invalid Content";
                    }

                }
            }

        } else {
            message = "Please LogIn";
        }

        JsonObject returnObject = new JsonObject();
        returnObject.addProperty("isSuccess", isSuccess);
        returnObject.addProperty("message", message);
        returnObject.addProperty("location", "send_chat");
        return returnObject;
    }

    public JsonObject LoginFilter(JsonObject jsonObject) {

        boolean isInvalid = false;
        String errorMessage = "";

        if (!jsonObject.has("mobile")) {

            isInvalid = true;
            errorMessage = "Mobile Cannot Be Found";

        } else if (!jsonObject.has("password")) {

            isInvalid = true;
            errorMessage = "Password Cannot Be Found";

        } else {

            String mobile = jsonObject.get("mobile").getAsString();
            String password = jsonObject.get("password").getAsString();

            if (mobile == null || mobile.trim().equals("")) {
                //no mobile
                isInvalid = true;
                errorMessage = "Missing Mobile Number";

            } else if (password == null || password.trim().equals("")) {
                //no password
                isInvalid = true;
                errorMessage = "Missing Password";

            } else {

                if (mobile.length() != 10) {
                    // too long
                    isInvalid = true;
                    errorMessage = "Mobile Number Too Long";

                } else if (password.length() > 20) {
                    //password too long
                    isInvalid = true;
                    errorMessage = "Password Too Long";

                } else if (!Validation.isValidMobile(mobile)) {
                    //invalid mobile format 
                    isInvalid = true;
                    errorMessage = "Invalid Mobile Number Format";

                } else if (!Validation.isValidPassword(password)) {
                    //invalid password
                    isInvalid = true;
                    errorMessage = "Invalid Password Format";

                } else {

                    isInvalid = false;
                }

            }
        }

        JsonObject returnObject = new JsonObject();
        returnObject.addProperty("isSuccess", !isInvalid);
        returnObject.addProperty("message", errorMessage);
        returnObject.addProperty("location", "login");
        return returnObject;

    }
}
