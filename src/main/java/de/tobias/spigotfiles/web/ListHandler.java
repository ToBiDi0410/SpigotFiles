package de.tobias.spigotfiles.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.tobias.spigotfiles.Main;
import de.tobias.spigotfiles.filedb.FileEntry;
import de.tobias.spigotfiles.configs.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ListHandler extends HttpServlet {

    public static Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

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

        if(!req.getParameterMap().containsKey("path")) {
            resp.setStatus(400);
            resp.getWriter().write("REQUIRED_FIELD;path");
            resp.getWriter().close();
            return;
        }

        String path = req.getParameter("path");
        File f = new File(Bukkit.getWorldContainer(), path);

        FileEntry baseFile = Main.pl.fileDB.getEntryByFile(f);
        if(baseFile == null) {
            resp.setStatus(404);
            resp.getWriter().write("FILE_NOT_FOUND");
            resp.getWriter().close();
            return;
        }

        HashMap<String, Object> values = new HashMap<>();

        ArrayList<FileEntry> children =  baseFile.getChildren();
        children.forEach(FileEntry::getTransactions);
        values.put("children", children);

        baseFile.getTransactions();
        values.put("self", baseFile);
        String result = gson.toJson(values);

        resp.setStatus(200);
        resp.setContentType("application/json");
        resp.getWriter().write(result);
        resp.getWriter().close();
    }
}
