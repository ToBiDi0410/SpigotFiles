package de.tobias.spigotfiles.filedb;

import de.tobias.mcutils.bukkit.BukkitLogger;
import de.tobias.mcutils.shared.AIODatabase;
import de.tobias.mcutils.shared.DatabaseUtils.DatabaseObjectTable;
import de.tobias.spigotfiles.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class FileDB {

    public AIODatabase db;
    public BukkitLogger logger;

    public DatabaseObjectTable<FileEntry> fileIndexTable;
    public DatabaseObjectTable<FileTransaction> fileTransactionTable;

    public FileDB() {
        logger = new BukkitLogger("§5FileDB §7| ", Main.pl.mainLogger);
        db = new AIODatabase(Main.pl.getDataFolder(), logger);
        fileIndexTable = new DatabaseObjectTable<>(FileEntry.class, "indexes", db, logger);
        fileIndexTable.enableCaching = false;
        fileTransactionTable = new DatabaseObjectTable<>(FileTransaction.class, "transactions", db, logger);
        //logger.debug = true;
        //fileIndexTable.logger.debug = true;
    }

    public ArrayList<FileTransaction> getTransactionsByFile(FileEntry entry) {
        return fileTransactionTable.getAllByField("FILE_ID", entry.getID()).stream().sorted(Comparator.comparingLong(a -> a.date)).collect(Collectors.toCollection(ArrayList::new));
    }

    public FileEntry getEntryByFile(File file) {
        return fileIndexTable.getAllByField("PATH", file.getAbsolutePath()).stream().findAny().orElse(null);
    }
}
