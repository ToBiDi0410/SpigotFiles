package de.tobias.spigotfiles.web;

import de.tobias.spigotfiles.configs.UserPermission;
import de.tobias.spigotfiles.filedb.FileManager;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;

public class CopyHandler extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        WrappedRequest wReq = new WrappedRequest(req, resp);
        if(!wReq.ensureUser()) return;
        if(!wReq.ensurePermission(UserPermission.WRITE)) return;
        if(!wReq.ensureFile("source")) return;
        if(!wReq.ensureFile("target")) return;

        File source = wReq.getFileByParameter("source");
        File target = wReq.getFileByParameter("target");

        if(!target.isDirectory()) {
            wReq.respond(401, "TARGET_NO_DIR");
            return;
        }

        File literalTarget = new File(target, source.getName());
        if(FileManager.copy(wReq.getUser(), source, literalTarget)) {
            wReq.respond(200, "COPIED");
        } else {
            wReq.respond(500, "FAILURE");

        }
    }
}
