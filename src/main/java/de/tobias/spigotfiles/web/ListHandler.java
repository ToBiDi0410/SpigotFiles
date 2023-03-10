package de.tobias.spigotfiles.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.tobias.spigotfiles.Main;
import de.tobias.spigotfiles.filedb.FileEntry;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ListHandler extends HttpServlet {

    public static Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WrappedRequest wReq = new WrappedRequest(req, resp);
        if(!wReq.ensureUser()) return;
        if(!wReq.ensureFile("path", false)) return;

        File f = wReq.getFileByParameter("path");
        FileEntry baseFile = Main.pl.fileDB.getEntryByFile(f);
        if(baseFile == null) {
            wReq.respond(404, "FILE_NOT_FOUND");
            return;
        }

        HashMap<String, Object> values = new HashMap<>();

        ArrayList<FileEntry> children =  baseFile.getChildren();
        children.forEach(FileEntry::getTransactions);
        values.put("children", children);

        baseFile.getTransactions();
        values.put("self", baseFile);
        String result = gson.toJson(values);

        resp.setContentType("application/json");
        wReq.respond(200, result);
    }
}
