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
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DownloadDirHandler extends HttpServlet {

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

        if(!f.isDirectory()) {
            resp.setStatus(400);
            resp.getWriter().write("INVALID_FILE_TYPE;file");
            resp.getWriter().close();
            return;
        }

        resp.addHeader("Content-Disposition", "attachment; filename=\"" + f.getName() + ".zip\"");
        resp.setStatus(200);
        ZipOutputStream zipOut = new ZipOutputStream(resp.getOutputStream());
        zipFile(f, f.getName(), zipOut);
        zipOut.close();
        resp.getOutputStream().close();
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}
