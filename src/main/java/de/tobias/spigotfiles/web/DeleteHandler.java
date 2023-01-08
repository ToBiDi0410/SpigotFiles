package de.tobias.spigotfiles.web;

import de.tobias.spigotfiles.Main;
import de.tobias.spigotfiles.filedb.FileManager;
import de.tobias.spigotfiles.users.User;
import de.tobias.spigotfiles.users.UserPermission;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class DeleteHandler extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");

        HttpSession session = req.getSession();
        User u = (User) session.getAttribute("user");
        if(u == null) {
            resp.setStatus(401);
            resp.getWriter().write("LOGIN_REQUIRED");
            resp.getWriter().close();
            return;
        }

        if(!u.permissions.contains(UserPermission.WRITE)) {
            resp.setStatus(401);
            resp.getWriter().write("NO_PERMISSION");
            resp.getWriter().close();
            return;
        }

        if(!req.getParameterMap().containsKey("path")) {
            resp.setStatus(400);
            resp.getWriter().write("REQUIRED_FIELD;path");
            resp.getWriter().close();
            return;
        }

        String path = req.getParameter("path");
        File f = new File(Bukkit.getWorldContainer(), path);
        if(path.startsWith("C:\\")) f = new File(path);

        if(!f.exists()) {
            resp.setStatus(404);
            resp.getWriter().write("FILE_NOT_FOUND");
            resp.getWriter().close();
            return;
        }

        if(FileManager.delete(u, f)) {
            resp.setStatus(200);
            resp.getWriter().write("DELETED");
            resp.getWriter().close();
        } else {
            resp.setStatus(500);
            resp.getWriter().write("FAILURE");
            resp.getWriter().close();
        }
    }
}
