package controller;

import java.io.IOException;
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

        switch (category) {
            case "chat":
                request.getRequestDispatcher("/LoadChat").include(request, response);
                break;
            case "group":
                request.getRequestDispatcher("/LoadGroup").include(request, response);
                break;
            case "status":
                request.getRequestDispatcher("/LoadStatus").include(request, response);
                break;
            default:
                break;
        }
    }

}
