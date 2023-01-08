package de.tobias.spigotfiles.web;

import de.tobias.spigotfiles.users.User;
import de.tobias.spigotfiles.users.UserPermission;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DownloadHandler extends HttpServlet {

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

        if(!u.permissions.contains(UserPermission.READ)) {
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

        if(f.isDirectory()) {
            resp.setStatus(400);
            resp.getWriter().write("INVALID_FILE_TYPE;directory");
            resp.getWriter().close();
            return;
        }

        resp.addHeader("Content-Disposition", "attachment; filename=\"" + f.getName() + "\"");
        resp.setStatus(200);
        IOUtils.copy(new FileReader(f), resp.getWriter());
        resp.getWriter().close();
    }
}
