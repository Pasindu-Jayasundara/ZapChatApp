package model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entitiy.User;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

@WebFilter(urlPatterns = {"/VerifyUser"})
public class VerifyUserFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        Gson gson = new Gson();
        JsonObject fromJson = gson.fromJson(request.getReader(), JsonObject.class);
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        boolean isInvalid = false;
        String errorMessage = "";

        if (!fromJson.has("otp")) {

            isInvalid = true;
            errorMessage = "Otp Cannot Be Found";

        } else if (httpServletRequest.getSession(false) == null) {

            isInvalid = true;
            errorMessage = "Session Timeout";

        } else {
            String otp = fromJson.get("token").getAsString();

            if (otp.length() != 8) {
                //invalid token length
                isInvalid = true;
                errorMessage = "Invalid OTP Length";

            } else if (Validation.isInteger(otp)) {
                //invalid token length
                isInvalid = true;
                errorMessage = "Invalid OTP Type";

            } else {

                User user = (User) httpServletRequest.getSession(false).getAttribute("user");
                int sessionOtp = user.getOtp();

                if (sessionOtp != Integer.parseInt(otp)) {
                    //invalid token length
                    isInvalid = true;
                    errorMessage = "Invalid OTP";

                } else {
                    chain.doFilter(request, response);
                }

            }

        }
        if (isInvalid) {
            Response_DTO response_DTO = new Response_DTO(false, errorMessage);
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(response_DTO));
        }

    }

    @Override
    public void destroy() {
    }

}
