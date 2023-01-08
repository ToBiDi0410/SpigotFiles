package de.tobias.spigotfiles.filedb;

import de.tobias.mcutils.shared.DatabaseUtils.DatabaseObjectTableEntry;

public class FileTransaction extends DatabaseObjectTableEntry<FileTransaction> {

    public String FILE_ID;
    public String type;
    public String user;
    public Long date;

    public String additionalData = "";
}
