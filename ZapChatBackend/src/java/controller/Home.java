package controller;

import com.google.gson.Gson;
import dto.Response_DTO;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "Home", urlPatterns = {"/Home"})
public class Home extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String category = (String) request.getAttribute("category");

        if (category.equals("chat")) {
            request.getRequestDispatcher("/LoadChat").include(request, response);

        } else if (category.equals("group")) {
            request.getRequestDispatcher("/LoadGroup").include(request, response);

        } else if (category.equals("status")) {
            request.getRequestDispatcher("/LoadStatus").include(request, response);

        }
    }

}
