package de.tobias.spigotfiles.web;

import de.tobias.spigotfiles.Main;
import de.tobias.spigotfiles.users.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class LoginHandler extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Access-Control-Allow-Credentials", "true");

        HttpSession session = req.getSession();
        User u = (User) session.getAttribute("user");
        if(u != null) {
            resp.setStatus(400);
            resp.getWriter().write("ALREADY_LOGGED_IN");
            resp.getWriter().close();
            return;
        }

        if(!req.getParameterMap().containsKey("username")) {
            resp.setStatus(400);
            resp.getWriter().write("REQUIRED_FIELD;username");
            resp.getWriter().close();
            return;
        }

        if(!req.getParameterMap().containsKey("password")) {
            resp.setStatus(400);
            resp.getWriter().write("REQUIRED_FIELD;password");
            resp.getWriter().close();
            return;
        }

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        User user = Main.pl.userManager.getByName(username);
        if(user == null) {
            resp.setStatus(404);
            resp.getWriter().write("UNKNOWN_USER");
            resp.getWriter().close();
            return;
        }

        if(!user.isValidPassword(password)) {
            resp.setStatus(401);
            resp.getWriter().write("INVALID_PASSWORD");
            resp.getWriter().close();
            return;
        }

        session.setAttribute("user", user);
        resp.setStatus(200);
        resp.getWriter().write("LOGGED_IN");
        resp.getWriter().close();
    }
}
