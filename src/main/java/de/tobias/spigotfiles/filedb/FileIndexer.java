package de.tobias.spigotfiles.filedb;

import de.tobias.mcutils.bukkit.BukkitLogger;
import de.tobias.spigotfiles.Main;
import de.tobias.spigotfiles.configs.Settings;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.bukkit.Bukkit;

import java.io.File;

public class FileIndexer {

    public static BukkitLogger logger = new BukkitLogger("§6FileIndexer §7| ", Main.pl.mainLogger);
    public static Tika tika = new Tika();

    public static void indexAll() {
        logger.warn("Indexing all files (this may take a while)...");
        indexFile(Bukkit.getWorldContainer());
        logger.info("§aAll files have been indexed!");
    }

    public static void indexFolder(File folder) {
        for(File file : folder.listFiles()) {
            indexFile(file);
        }
    }

    public static void indexFile(File file, boolean indexFolder) {
        //logger.info("Indexing File: " + file.getAbsolutePath());
        if(Settings.isExcluded(file)) return;

        FileEntry alreadyEntry = Main.pl.fileDB.getEntryByFile(file);
        if (alreadyEntry == null) {
            logger.info("Indexing new File/Folder: " + file.getAbsolutePath());

            FileEntry entry = new FileEntry();
            entry.PATH = file.getAbsolutePath();
            entry.firstIndex = System.currentTimeMillis();
            entry.updateIndex();
            if(!entry.create(Main.pl.fileDB.fileIndexTable)) logger.error("Failed to index: " + file.getAbsolutePath());

        }
        if(file.isDirectory() && indexFolder) indexFolder(file);
    }

    public static void indexFile(File file) {
        indexFile(file, true);
    }

    public static void removeIndex(File file) {
        FileEntry alreadyEntry = Main.pl.fileDB.getEntryByFile(file);
        if(alreadyEntry != null) {
            for(FileEntry childEntry : alreadyEntry.getChildren()) removeIndex(childEntry.getAsFile());
            alreadyEntry.drop();
            logger.info("Dropped index: " + file.getAbsolutePath());
        }
    }

    public static void updateAll() {
        logger.warn("Updating all indexes (this may take a while)...");
        for(FileEntry entry : Main.pl.fileDB.fileIndexTable.getAll(100000)) {
            if(Settings.isExcluded(entry.getAsFile())) {
                removeIndex(entry.getAsFile());
            } else {
                if(entry.updateIndex()) entry.save();
            }
        }
        logger.info("§aAll indexes have been updated!");

    }
}
