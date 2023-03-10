package de.tobias.spigotfiles.web;

import de.tobias.spigotfiles.configs.UserPermission;
import de.tobias.spigotfiles.filedb.FileManager;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;

public class DeleteHandler extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        WrappedRequest wReq = new WrappedRequest(req, resp);
        if(!wReq.ensureUser()) return;
        if(!wReq.ensurePermission(UserPermission.WRITE)) return;
        if(!wReq.ensureFile("path")) return;

        File f = wReq.getFileByParameter("path");
        if(FileManager.delete(wReq.getUser(), f)) {
            wReq.respond(200, "DELETED");
        } else {
            wReq.respond(500, "FAILURE");
        }
    }
}
