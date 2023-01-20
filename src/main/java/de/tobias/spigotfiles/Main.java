package de.tobias.spigotfiles;

import de.tobias.mcutils.bukkit.BukkitAutoUpdater;
import de.tobias.mcutils.bukkit.BukkitLogger;
import de.tobias.spigotfiles.commands.AddUserCommand;
import de.tobias.spigotfiles.commands.ChangePasswordCommand;
import de.tobias.spigotfiles.commands.RemoveUserCommand;
import de.tobias.spigotfiles.commands.SetPermissionCommand;
import de.tobias.spigotfiles.configs.Settings;
import de.tobias.spigotfiles.filedb.FileDB;
import de.tobias.spigotfiles.filedb.FileIndexer;
import de.tobias.spigotfiles.configs.UserManager;
import de.tobias.spigotfiles.web.JettyServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Collections;

public class Main extends JavaPlugin {

    public static Main pl;
    public BukkitLogger mainLogger = new BukkitLogger("§bSpigotFiles §7| ");
    public FileDB fileDB;
    public JettyServer jettyServer;
    public UserManager userManager;
    public Settings settings;
    public FileSystem webFolderFS;

    @Override
    public void onEnable() {
        pl = this;
        mainLogger.info("Loading Plugin...");

        Metrics metrics = new Metrics(this, 17350);

        settings = new Settings(new File(getDataFolder(), "settings.yml"), mainLogger);
        if(!settings.doAll()) throw new RuntimeException("Settings file invalid! Please fix this or delete the file");

        if(Settings.ENABLE_AUTO_UPDATER) {
            BukkitAutoUpdater updater = new BukkitAutoUpdater(mainLogger, this, "https://github.com/ToBiDi0410/SpigotFiles/raw/build/target/SpigotFiles-1.0-SNAPSHOT-jar-with-dependencies.jar", "https://raw.githubusercontent.com/ToBiDi0410/SpigotFiles/build/src/main/resources/plugin.yml");
            if(updater.checkForUpdateAndUpdate()) return;
        }

        try {
            URI webFolderURI = Main.pl.getClass().getResource("/www").toURI();
            webFolderFS = FileSystems.newFileSystem(webFolderURI, Collections.emptyMap());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to prepare web resources");
        }



        jettyServer = new JettyServer(Settings.WEB_PORT);
        if(!jettyServer.start()) throw new RuntimeException("Failed to start webserver (check above)");

        userManager = new UserManager(new File(getDataFolder(), "users.yml"));
        userManager.init();

        fileDB = new FileDB();
        if(!fileDB.db.connect()) throw new RuntimeException("Failed to connect to database (check above)");
        fileDB.fileIndexTable.init();
        fileDB.fileTransactionTable.init();

        FileIndexer.indexAll();
        FileIndexer.updateAll();

        Bukkit.getPluginCommand("sf-adduser").setExecutor(new AddUserCommand());
        Bukkit.getPluginCommand("sf-removeuser").setExecutor(new RemoveUserCommand());
        Bukkit.getPluginCommand("sf-setperm").setExecutor(new SetPermissionCommand());
        Bukkit.getPluginCommand("sf-changepass").setExecutor(new ChangePasswordCommand());

        mainLogger.info("§aPlugin loaded successfully!");
    }

    @Override
    public void onDisable() {
        if(userManager != null) userManager.save();
        if(fileDB != null) fileDB.db.disconnect();
        if(jettyServer != null) jettyServer.stop();
        try { if(webFolderFS != null) webFolderFS.close(); } catch (Exception ex) { ex.printStackTrace(); }
    }
}
