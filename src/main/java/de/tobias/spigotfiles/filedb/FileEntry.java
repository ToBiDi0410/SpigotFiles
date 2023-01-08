package de.tobias.spigotfiles.filedb;

import de.tobias.mcutils.shared.DatabaseUtils.DatabaseObjectTableEntry;
import de.tobias.spigotfiles.Main;

import java.io.File;
import java.nio.file.Files;
import java.sql.Array;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class FileEntry extends DatabaseObjectTableEntry<FileEntry> {

    public String PATH;

    public Long firstIndex;
    public Long lastUpdated;
    public Long size;

    public String mimeType;
    public Boolean exists;

    public File getAsFile() {
        return new File(PATH);
    }
    public ArrayList<FileTransaction> transactions;

    public ArrayList<FileTransaction> getTransactions() {
        if(transactions == null) transactions = Main.pl.fileDB.getTransactionsByFile(this);
        return transactions;
    }
    public boolean addTransaction(FileTransaction trans) {
        trans.FILE_ID = getID();
        trans.date = System.currentTimeMillis();
        return trans.create(Main.pl.fileDB.fileTransactionTable);
    }

    public ArrayList<FileEntry> getChildren() {
        File own = getAsFile();
        return Main.pl.fileDB.fileIndexTable.getAll(100000).stream().filter(a -> a.getAsFile().getParentFile().getAbsolutePath().equalsIgnoreCase(own.getAbsolutePath())).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<FileEntry> getExistentChildren() {
        return getChildren().stream().filter(a -> a.exists).collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean updateIndex() {
        File f = getAsFile();

        if(f.exists()) {
            if(lastUpdated != null && f.lastModified() <= lastUpdated) return false; //Do not update if it has not been changed since last update
        } else {
            if(!exists) return false; //Do not update if it does not exist and is already marked as this
        }
        FileIndexer.logger.info("Updating index of: " + PATH);

        //Create transactions if file has been deleted or added between restarts
        if(exists == null || f.exists() != exists) {
            FileTransaction changeTransaction = new FileTransaction();
            changeTransaction.type = f.exists() ? FileTransactionType.CREATE.name() : FileTransactionType.DELETE.name();
            changeTransaction.user = Main.pl.serverUser.getID();
            changeTransaction.additionalData = (exists == null ? "NEWLY_INDEXED" : "UNRECORDED");
            addTransaction(changeTransaction);
        }

        forceUpdateIndex();
        return true;
    }

    public void forceUpdateIndex() {
        //Determine Parameters if exists
        File f = getAsFile();
        lastUpdated = System.currentTimeMillis();
        exists = f.exists();

        if(f.exists()) {
            try {
                size = Files.size(f.toPath());
            } catch (Exception ex) {
                FileIndexer.logger.warn("Failed to determine size: " + f.getAbsolutePath());
                size = -1L;
            }

            if (!f.isDirectory()) {
                try {
                    mimeType = FileIndexer.tika.detect(f);
                } catch (Exception ex) {
                    FileIndexer.logger.warn("Failed to determine mime-type: " + f.getAbsolutePath());
                    mimeType = "UNKNOWN";
                }
            } else mimeType = "FOLDER";
        }
    }
}
