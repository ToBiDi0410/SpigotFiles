package de.tobias.spigotfiles.web;

import de.tobias.spigotfiles.Main;
import de.tobias.spigotfiles.users.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResolveUser extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");

        if(!req.getParameterMap().containsKey("id")) {
            resp.setStatus(400);
            resp.getWriter().write("id not specified");
            resp.getWriter().close();
            return;
        }
        String searchID = req.getParameter("id");

        User foundUser = Main.pl.userCache.stream().filter(a -> a.getID().equalsIgnoreCase(searchID)).findAny().orElse(null);
        resp.setStatus(foundUser != null ? 200 : 404);
        resp.getWriter().write(foundUser != null ? foundUser.username : "???");
        resp.getWriter().close();
    }
}
