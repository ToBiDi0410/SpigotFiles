package de.tobias.spigotfiles.web;

import de.tobias.spigotfiles.Main;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.progress.ProgressMonitor;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DownloadHandler extends HttpServlet {

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

        if(f.isDirectory()) {
            resp.setStatus(400);
            resp.getWriter().write("file is directory");
            resp.getWriter().close();
            return;
        }

        resp.addHeader("Content-Disposition", "attachment; filename=\"" + f.getName() + "\"");
        resp.setStatus(200);
        IOUtils.copy(new FileReader(f), resp.getWriter());
        resp.getWriter().close();
    }
}
