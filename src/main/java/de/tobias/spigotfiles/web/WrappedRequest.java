package de.tobias.spigotfiles.web;

import de.tobias.spigotfiles.Main;
import de.tobias.spigotfiles.configs.User;
import de.tobias.spigotfiles.configs.UserPermission;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.bukkit.Bukkit;

import java.io.File;

public class WrappedRequest {

    HttpServletRequest req;
    HttpServletResponse resp;

    public WrappedRequest(HttpServletRequest req, HttpServletResponse resp) {
        this.req = req;
        this.resp = resp;

        resp.addHeader("Access-Control-Allow-Origin", "*");
    }

    public User getUser() {
        HttpSession session = req.getSession();
        Object value = session.getAttribute("user");
        if(value == null) return null;
        User u = (User) session.getAttribute("user");
        return u;
    }

    public void respond(int code, String message) {
        try {
            resp.setStatus(code);
            resp.getWriter().write(message);
            resp.getWriter().close();
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean ensureUser() {
        if(getUser() == null) {
            respond(401, "LOGIN_REQUIRED");
            return false;
        }

        if(!Main.pl.userManager.users.contains(getUser())) {
            respond(401, "USER_NOT_FOUND");
            return false;
        }

        return true;
    }

    public boolean ensurePermission(UserPermission perm) {
        if(!getUser().permissions.contains(perm)) {
            respond(401, "NO_PERMISSION;" + perm.name());
            return false;
        }
        return true;
    }

    public boolean ensureParameter(String param) {
        if(!req.getParameterMap().containsKey(param)) {
            respond(400, "REQUIRED_FILED;" + param);
            return false;
        }
        return true;
    }

    public boolean ensureFile(String param) {
        return ensureFile(param, true);
    }

    public boolean ensureFile(String param, boolean hasToExist) {
        if(!ensureParameter(param)) return false;

        File f = getFileByParameter(param);
        if(f == null || (!f.exists() && hasToExist)) {
            respond(400, "FILE_NOT_FOUND;" + param);
            return false;
        }

        return true;
    }

    public File getFileByParameter(String param) {
        String path = req.getParameter(param);
        File f = new File(Bukkit.getWorldContainer(), path);
        if(path.startsWith("C:\\")) f = new File(path);
        return f;
    }
}
