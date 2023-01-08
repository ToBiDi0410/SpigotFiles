package de.tobias.spigotfiles;

import de.tobias.mcutils.bukkit.BukkitLogger;
import de.tobias.spigotfiles.filedb.FileDB;
import de.tobias.spigotfiles.filedb.FileIndexer;
import de.tobias.spigotfiles.users.User;
import de.tobias.spigotfiles.users.UserManager;
import de.tobias.spigotfiles.web.JettyServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    public static Main pl;
    public BukkitLogger mainLogger = new BukkitLogger("§bSpigotFiles §7| ");
    public FileDB fileDB;
    public JettyServer jettyServer;
    public UserManager userManager;

    @Override
    public void onEnable() {
        pl = this;
        mainLogger.info("Loading Plugin...");

        //BukkitAutoUpdater updater = new BukkitAutoUpdater(mainLogger, this, "https://github.com/ToBiDi0410/JoinMePlus/raw/build/target/joinmeplus-1.0-jar-with-dependencies.jar", "https://raw.githubusercontent.com/ToBiDi0410/JoinMePlus/build/src/main/resources/bungee.yml");
        //if(updater.checkForUpdateAndUpdate()) return;

        jettyServer = new JettyServer(8123);
        if(!jettyServer.start()) throw new RuntimeException("Failed to start webserver (check above)");

        userManager = new UserManager(new File(getDataFolder(), "users.yml"));
        userManager.init();

        fileDB = new FileDB();
        if(!fileDB.db.connect()) throw new RuntimeException("Failed to connect to database (check above)");
        fileDB.fileIndexTable.init();
        fileDB.fileTransactionTable.init();

        FileIndexer.indexAll();
        FileIndexer.updateAll();

        mainLogger.info("§aPlugin loaded successfully!");

        if(userManager.users.size() < 2) {
            User testUser = new User("test");
            testUser.updatePassword("jesusishere");
            userManager.users.add(testUser);
            userManager.save();
        }
    }

    @Override
    public void onDisable() {
        if(userManager != null) userManager.save();
        if(fileDB != null) fileDB.db.disconnect();
        if(jettyServer != null) jettyServer.stop();
    }
}
