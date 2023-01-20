package de.tobias.spigotfiles;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class Utils {

    public static File ensureFile(HttpServletRequest req, HttpServletResponse resp, String parameter) throws IOException {
        if(!req.getParameterMap().containsKey(parameter)) {
            resp.setStatus(400);
            resp.getWriter().write("REQUIRED_FIELD;" + parameter);
            resp.getWriter().close();
            return null;
        }

        String sourcePath = req.getParameter(parameter);
        File sourceFile = new File(Bukkit.getWorldContainer(), sourcePath);
        if(sourcePath.startsWith("C:\\")) sourceFile = new File(sourcePath);

        if(!sourceFile.exists()) {
            resp.setStatus(404);
            resp.getWriter().write("FILE_NOT_FOUND;" + sourcePath);
            resp.getWriter().close();
            return null;
        }

        return sourceFile;
    }
}
