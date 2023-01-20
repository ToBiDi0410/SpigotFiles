package de.tobias.spigotfiles.web;

import de.tobias.spigotfiles.configs.UserPermission;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DownloadDirHandler extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WrappedRequest wReq = new WrappedRequest(req, resp);
        if(!wReq.ensureUser()) return;
        if(!wReq.ensurePermission(UserPermission.READ)) return;
        if(!wReq.ensureFile("path")) return;

        File f = wReq.getFileByParameter("path");
        if(!f.isDirectory()) {
            wReq.respond(400, "FILE_NEEDS_TO_BE_DIRECTORY");
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
