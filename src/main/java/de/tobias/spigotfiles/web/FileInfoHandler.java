package de.tobias.spigotfiles.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.tobias.spigotfiles.Main;
import de.tobias.spigotfiles.configs.UserPermission;
import de.tobias.spigotfiles.filedb.FileEntry;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;

public class FileInfoHandler extends HttpServlet {

    public static Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WrappedRequest wReq = new WrappedRequest(req, resp);
        if(!wReq.ensureUser()) return;
        if(!wReq.ensurePermission(UserPermission.READ)) return;
        if(!wReq.ensureFile("path", false)) return;

        File f = wReq.getFileByParameter("path");
        FileEntry baseFile = Main.pl.fileDB.getEntryByFile(f);
        if(baseFile == null) {
            wReq.respond(404, "FILE_NOT_FOUND");
            return;
        }

        baseFile.getTransactions();
        String result = gson.toJson(baseFile);
        resp.setContentType("application/json");
        wReq.respond(200, result);
    }
}
