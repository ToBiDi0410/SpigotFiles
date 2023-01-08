package de.tobias.spigotfiles.web;

import de.tobias.spigotfiles.filedb.FileManager;
import de.tobias.spigotfiles.configs.User;
import de.tobias.spigotfiles.configs.UserPermission;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class UploadHandler extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        File dir = new File(Bukkit.getWorldContainer(), path);
        if(path.startsWith("C:\\")) dir = new File(path);

        Part file = req.getPart("file");
        if(file == null) {
            resp.setStatus(400);
            resp.getWriter().write("MISSING_PART;file");
            resp.getWriter().close();
            return;
        }

        File f = new File(dir, file.getSubmittedFileName());

        if(!FileManager.upload(u, dir, file)) {
            resp.setStatus(500);
            resp.getWriter().write("FAILURE");
            resp.getWriter().close();
            return;
        }

        resp.setStatus(200);
        resp.getWriter().write("COMPLETED");
        resp.getWriter().close();
    }
}
