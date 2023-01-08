package de.tobias.spigotfiles.web;

import de.tobias.spigotfiles.Main;
import de.tobias.spigotfiles.configs.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class ResolveUser extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");

        HttpSession session = req.getSession();
        User u = (User) session.getAttribute("user");
        if(u == null) {
            resp.setStatus(401);
            resp.getWriter().write("LOGIN_REQUIRED");
            resp.getWriter().close();
            return;
        }

        if(!req.getParameterMap().containsKey("id")) {
            resp.setStatus(400);
            resp.getWriter().write("REQUIRED_FIELD;id");
            resp.getWriter().close();
            return;
        }
        String searchID = req.getParameter("id");
        User foundUser = Main.pl.userManager.users.stream().filter(a -> a.ID.equalsIgnoreCase(searchID)).findAny().orElse(null);
        resp.setStatus(foundUser != null ? 200 : 404);
        resp.getWriter().write(foundUser != null ? foundUser.username : "???");
        resp.getWriter().close();
    }
}
