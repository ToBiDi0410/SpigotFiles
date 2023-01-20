package de.tobias.spigotfiles.web;

import de.tobias.spigotfiles.configs.UserPermission;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DownloadHandler extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WrappedRequest wReq = new WrappedRequest(req, resp);
        if(!wReq.ensureUser()) return;
        if(!wReq.ensurePermission(UserPermission.READ)) return;
        if(!wReq.ensureFile("path")) return;

        File f = wReq.getFileByParameter("path");

        if(f.isDirectory()) {
            wReq.respond(400, "FILE_NEEDS_TO_BE_FILE");
            return;
        }

        resp.addHeader("Content-Disposition", "attachment; filename=\"" + f.getName() + "\"");
        resp.setStatus(200);
        IOUtils.copy(new FileReader(f), resp.getWriter());
        resp.getWriter().close();
    }
}
