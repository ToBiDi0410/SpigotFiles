package de.tobias.spigotfiles.web;

import de.tobias.spigotfiles.Main;
import de.tobias.spigotfiles.filedb.FileManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class DeleteHandler extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");

        if(!req.getParameterMap().containsKey("path")) {
            resp.setStatus(400);
            resp.getWriter().write("path not specified");
            resp.getWriter().close();
            return;
        }

        String path = req.getParameter("path");
        File f = new File(Bukkit.getWorldContainer(), path);
        if(path.startsWith("C:\\")) f = new File(path);

        if(!f.exists()) {
            resp.setStatus(404);
            resp.getWriter().write("file not found");
            resp.getWriter().close();
            return;
        }

        if(FileManager.delete(Main.pl.serverUser, f)) {
            resp.setStatus(200);
            resp.getWriter().write("deleted");
            resp.getWriter().close();
        } else {
            resp.setStatus(500);
            resp.getWriter().write("failed");
            resp.getWriter().close();
        }
    }
}
