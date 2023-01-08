package de.tobias.spigotfiles.web;

import de.tobias.spigotfiles.configs.User;
import de.tobias.spigotfiles.configs.UserPermission;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class LoginStateHandler extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Access-Control-Allow-Credentials", "true");

        HttpSession session = req.getSession();
        User u = (User) session.getAttribute("user");
        if(u == null) {
            resp.setStatus(404);
            resp.getWriter().write("NOT_LOGGED_IN");
            resp.getWriter().close();
            return;
        }

        resp.setStatus(200);
        String joinedPerms = "";
        for (UserPermission permission : u.permissions) {
            joinedPerms += permission.name() + ",";
        }
        if(joinedPerms.endsWith(",")) joinedPerms = joinedPerms.substring(0, joinedPerms.length()-1);

        resp.getWriter().write("LOGGED_IN;" + u.ID + ";" + u.username + ";" + joinedPerms);
        resp.getWriter().close();
    }
}
