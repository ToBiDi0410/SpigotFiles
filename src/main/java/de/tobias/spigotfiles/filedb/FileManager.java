package de.tobias.spigotfiles.filedb;

import de.tobias.mcutils.bukkit.BukkitLogger;
import de.tobias.spigotfiles.Main;
import de.tobias.spigotfiles.users.User;
import jakarta.servlet.http.Part;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class FileManager {

    public static BukkitLogger logger = new BukkitLogger("§3FileManager §7| ", Main.pl.mainLogger);

    public static boolean move(User u, File source, File target) {
        try {
            if(!source.exists()) throw new Exception("Source does not exist");

            FileIndexer.indexFile(source);
            FileIndexer.removeIndex(target);
            FileEntry sourceEntry = Main.pl.fileDB.getEntryByFile(source);
            Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);

            sourceEntry.PATH = target.getAbsolutePath();
            sourceEntry.save();

            FileTransaction moveTransaction = new FileTransaction();
            moveTransaction.type = FileTransactionType.MOVE.name();
            moveTransaction.user = u.ID;
            moveTransaction.additionalData = source.getAbsolutePath() + ";;;" + target.getAbsolutePath();
            sourceEntry.addTransaction(moveTransaction);

            logger.info("§aMoved file '" + source.getAbsolutePath() + "' to '" + target.getAbsolutePath() + "'!");
            return true;
        } catch (Exception ex) {
            logger.error("Failed to move file '" + source.getAbsolutePath() + "' to '" + target.getAbsolutePath() + "':");
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean rename(User u, File source, String newName) {
        try {
            if(!source.exists()) throw new Exception("Source does not exist");
            File target = new File(source.getParentFile(), newName);
            if(target.exists()) throw new Exception("Name is already used");

            FileIndexer.indexFile(source);
            FileIndexer.removeIndex(target);
            FileEntry sourceEntry = Main.pl.fileDB.getEntryByFile(source);
            Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);

            sourceEntry.PATH = target.getAbsolutePath();
            sourceEntry.save();

            FileTransaction renameTransaction = new FileTransaction();
            renameTransaction.type = FileTransactionType.RENAME.name();
            renameTransaction.user = u.ID;
            renameTransaction.additionalData = source.getName() + ";;;" + target.getName();
            sourceEntry.addTransaction(renameTransaction);

            logger.info("§aRenamed file '" + source.getName() + "' to '" + target.getName() + "' (IN: " + source.getParentFile().getAbsolutePath() + ")");
            return true;
        } catch (Exception ex) {
            logger.error("Failed to rename file '" + source.getAbsolutePath() + "' to '" + newName + "':");
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean copy(User u, File source, File target) {
        try {
            if(!source.exists()) throw new Exception("Source does not exist");

            FileIndexer.indexFile(source);
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            FileIndexer.indexFile(target);

            FileEntry sourceEntry = Main.pl.fileDB.getEntryByFile(source);
            FileEntry targetEntry = Main.pl.fileDB.getEntryByFile(target);

            FileTransaction copyToTransaction = new FileTransaction();
            copyToTransaction.type = FileTransactionType.COPY_TO.name();
            copyToTransaction.user = u.ID;
            copyToTransaction.additionalData = targetEntry.PATH;
            sourceEntry.addTransaction(copyToTransaction);

            FileTransaction copyFromTransaction = new FileTransaction();
            copyFromTransaction.type = FileTransactionType.COPY_FROM.name();
            copyFromTransaction.user = u.ID;
            copyFromTransaction.additionalData = sourceEntry.PATH;
            targetEntry.addTransaction(copyFromTransaction);

            logger.info("§aCopied file '" + source.getAbsolutePath() + "' to '" + target.getAbsolutePath() + "'!");
            return true;
        } catch (Exception ex) {
            logger.error("Failed to copy file '" + source.getAbsolutePath() + "' to '" + target.getAbsolutePath() + "':");
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean delete(User u, File source) {
        try {
            if(!source.exists()) throw new Exception("Source does not exist");

            FileIndexer.indexFile(source);

            if(source.isDirectory()) FileUtils.deleteDirectory(source);
            else FileUtils.delete(source);

            FileEntry sourceEntry = Main.pl.fileDB.getEntryByFile(source);

            FileTransaction deleteTransaction = new FileTransaction();
            deleteTransaction.type = FileTransactionType.DELETE.name();
            deleteTransaction.user = u.ID;
            sourceEntry.addTransaction(deleteTransaction);

            sourceEntry.exists = false;
            sourceEntry.forceUpdateIndex();
            sourceEntry.save();

            logger.info("§cDeleted File: " + source.getAbsolutePath() + "");
            return true;
        } catch (Exception ex) {
            logger.error("Failed to delete file '" + source.getAbsolutePath() + "':");
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean upload(User u, File dir, Part file) {
        try {
            long startUpload = System.currentTimeMillis();
            if(!dir.exists()) throw new Exception("Directory does not exist");

            FileIndexer.indexFile(dir);
            File target = new File(dir, file.getSubmittedFileName());
            boolean existedBefore = target.exists();
            Files.copy(file.getInputStream(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            FileIndexer.indexFile(target);

            FileEntry targetEntry = Main.pl.fileDB.getEntryByFile(target);

            if(!existedBefore) {
                FileTransaction uploadTransaction = new FileTransaction();
                uploadTransaction.type = FileTransactionType.ADD.name();
                uploadTransaction.user = u.ID;
                uploadTransaction.date = startUpload;
                targetEntry.addTransaction(uploadTransaction);
            } else {
                FileTransaction updateTransaction = new FileTransaction();
                updateTransaction.type = FileTransactionType.UPDATE.name();
                updateTransaction.user = u.ID;
                updateTransaction.date = startUpload;
                targetEntry.addTransaction(updateTransaction);
            }

            targetEntry.exists = true;
            targetEntry.forceUpdateIndex();
            targetEntry.save();

            logger.info("§aUploaded File: " + target.getAbsolutePath() + "");
            return true;
        } catch (Exception ex) {
            logger.error("Failed to upload/save file '" + file.getSubmittedFileName() + "':");
            ex.printStackTrace();
            return false;
        }
    }
}
