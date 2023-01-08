package de.tobias.spigotfiles.web;

import de.tobias.mcutils.bukkit.BukkitLogger;
import de.tobias.spigotfiles.Main;
import jakarta.servlet.MultipartConfigElement;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class JettyServer {

    public BukkitLogger logger;
    public Integer port;
    public Server server;
    public ServletContextHandler handler;

    public JettyServer(Integer port) {
        logger = new BukkitLogger("§2Jetty §7| ", Main.pl.mainLogger);
        this.port = port;
        System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
        System.setProperty("org.eclipse.jetty.LEVEL", "OFF");
    }

    public boolean start() {
        logger.info("Starting webserver with port: " + port);

        try {
            server = new Server();
            ServerConnector connector = new ServerConnector(server);
            connector.setPort(port);
            server.setConnectors(new Connector[] {connector});

            HandlerList handlerList = new HandlerList();
            handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            handlerList.setHandlers(new Handler[] { handler });
            server.setHandler(handlerList);

            handler.setContextPath("/");
            handler.addServlet(ListHandler.class, "/api/list");
            handler.addServlet(ResolveUser.class, "/api/username");
            handler.addServlet(DownloadHandler.class, "/api/download");
            handler.addServlet(DownloadDirHandler.class, "/api/downloadDir");
            handler.addServlet(DeleteHandler.class, "/api/delete");
            handler.addServlet(LoginHandler.class, "/api/login");
            handler.addServlet(LoginStateHandler.class, "/api/loginstate");
            handler.addServlet(UploadHandler.class, "/api/upload").getRegistration().setMultipartConfig(new MultipartConfigElement(""));
            handler.addServlet(MainServlet.class, "/*");

            server.start();
            logger.info("§aServer started successfully!");
            return true;
        } catch (Exception ex) {
            logger.error("Failed to start webserver:");
            ex.printStackTrace();
            return false;
        }
    }

    public boolean stop() {
        if(server == null) return true;
        logger.info("Stopping webserver...");
        try {
            server.stop();
            logger.info("§aServer stopped successfully!");
            return true;
        } catch (Exception ex) {
            logger.error("Failed to stop webserver:");
            ex.printStackTrace();
            return false;
        }
    }

}
