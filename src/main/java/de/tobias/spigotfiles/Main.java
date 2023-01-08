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

        //File testFile100 = new File(Bukkit.getWorldContainer(), "testtesttest.json");
        //FileManager.copy(serverUser, testFile100, new File(Bukkit.getWorldContainer(), "test-copy.json"));

        /*File testFile100 = new File(Bukkit.getWorldContainer(), "banned-players.json");
        FileEntry testFile100E = fileDB.getEntryByFile(testFile100);
        System.out.println(fileDB.getTransactionsByFile(testFile100E).size());*/

        /*FileEntry rootEntry = fileDB.getEntryByFile(Bukkit.getWorldContainer());
        long start = System.currentTimeMillis();
        ArrayList<FileEntry> children = rootEntry.getChildren();
        System.out.println("Operation took: " + (System.currentTimeMillis()-start));
        for(FileEntry entries : children) {
            System.out.println(entries.getAsFile().getAbsolutePath() + " (" + (entries.exists ? "EXISTS" : "DELETED") +")");
        }*/

        //File testSource = new File(Bukkit.getWorldContainer(), "ops.json");
        //FileIndexer.indexFile(testSource);

        /*

        Bukkit.getScheduler().runTaskLater(this, () -> {
            File testSource = new File(Bukkit.getWorldContainer(), "ops.json");
            File testDestination = new File(Bukkit.getWorldContainer(), Math.random() + ".json");
            FileManager.copy(serverUser, testSource, testDestination);
            FileManager.rename(serverUser, testDestination, "TESTFILE.json");

            File f = new File(Bukkit.getWorldContainer(), "TESTFILE.json");
            FileManager.delete(serverUser, f);

            FileEntry fE = fileDB.getEntryByFile(f);
            ArrayList<FileTransaction> transactions = fE.getTransactions();
            mainLogger.info("------- [FILE TRANS] -------");
            for(FileTransaction trans : transactions) {
                mainLogger.info(trans.type + ": " + trans.additionalData);
            }
        }, 20L);*/
    }

    @Override
    public void onDisable() {
        if(userManager != null) userManager.save();
        if(fileDB != null) fileDB.db.disconnect();
        if(jettyServer != null) jettyServer.stop();
    }
}
