package de.tobias.spigotfiles.web;

import de.tobias.spigotfiles.configs.UserPermission;
import de.tobias.spigotfiles.filedb.FileManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;

public class UploadHandler extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WrappedRequest wReq = new WrappedRequest(req, resp);
        if(!wReq.ensureUser()) return;
        if(!wReq.ensurePermission(UserPermission.WRITE)) return;
        if(!wReq.ensureFile("path", true)) return;

        File dir = wReq.getFileByParameter("path");
        Part file = req.getPart("file");
        if(file == null) {
            wReq.respond(400, "MISSING_PART;file");
            return;
        }

        File f = new File(dir, file.getSubmittedFileName());
        if(!FileManager.upload(wReq.getUser(), dir, file)) {
            wReq.respond(500, "FAILURE");
            return;
        }

        wReq.respond(200, "UPLOADED");
    }
}
