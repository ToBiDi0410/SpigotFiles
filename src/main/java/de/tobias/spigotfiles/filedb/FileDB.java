package de.tobias.spigotfiles.filedb;

import de.tobias.mcutils.bukkit.BukkitLogger;
import de.tobias.mcutils.shared.AIODatabase;
import de.tobias.mcutils.shared.DatabaseUtils.DatabaseObjectTable;
import de.tobias.spigotfiles.Main;

import java.io.File;
import java.util.ArrayList;

public class FileDB {

    public AIODatabase db;
    public BukkitLogger logger;

    public DatabaseObjectTable<FileEntry> fileIndexTable;
    public DatabaseObjectTable<FileTransaction> fileTransactionTable;

    public FileDB() {
        logger = new BukkitLogger("§5FileDB §7| ", Main.pl.mainLogger);
        //logger.debug = true;
        db = new AIODatabase(Main.pl.getDataFolder(), logger);
        fileIndexTable = new DatabaseObjectTable<>(FileEntry.class, "indexes", db, logger);
        fileTransactionTable = new DatabaseObjectTable<>(FileTransaction.class, "transactions", db, logger);
    }

    public ArrayList<FileTransaction> getTransactionsByFile(FileEntry entry) {
        return fileTransactionTable.getAllByField("FILE_ID", entry.getID());
    }

    public FileEntry getEntryByFile(File file) {
        return fileIndexTable.getAllByField("PATH", file.getAbsolutePath()).stream().findAny().orElse(null);
    }
}
