package de.tobias.spigotfiles.web;

import com.google.common.io.Resources;
import de.tobias.spigotfiles.Main;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

public class MainServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getRequestURI().replace("/web", "");
        if (path.equalsIgnoreCase("/")) {
            path = "/index.html";
        }

        String respath = (String) "/www" + path;
        URL res = Main.pl.getClass().getResource(respath);
        byte[] content = Resources.toByteArray(res);
        if(content == null) {
            response.sendRedirect("404.html");
            return;
        }

        OutputStream outputStream = response.getOutputStream();
        String extension = FilenameUtils.getExtension(path);

        /*if(extension.equalsIgnoreCase("html") || extension.equalsIgnoreCase("js") || extension.equalsIgnoreCase("css")) {
            String fileContent = new String(content, StandardCharsets.UTF_8);
            content = fileContent.getBytes(StandardCharsets.UTF_8);
        }*/

        response.setStatus(HttpServletResponse.SC_OK);
        outputStream.write(content);
        outputStream.flush();
        outputStream.close();
    }
}
