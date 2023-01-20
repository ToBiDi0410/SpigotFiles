package de.tobias.spigotfiles.web;

import de.tobias.spigotfiles.Main;
import de.tobias.spigotfiles.configs.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResolveUser extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WrappedRequest wReq = new WrappedRequest(req, resp);
        if(!wReq.ensureUser()) return;
        if(!wReq.ensureParameter("id")) return;

        String searchID = req.getParameter("id");
        User foundUser = Main.pl.userManager.users.stream().filter(a -> a.ID.equalsIgnoreCase(searchID)).findAny().orElse(null);
        resp.setStatus(foundUser != null ? 200 : 404);
        resp.getWriter().write(foundUser != null ? foundUser.username : "???");
        resp.getWriter().close();
    }
}
