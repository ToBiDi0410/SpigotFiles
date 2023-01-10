package de.tobias.spigotfiles.configs;

import de.tobias.mcutils.bukkit.BukkitLogger;
import de.tobias.mcutils.bukkit.BukkitStaticClassSerializer;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;

public class Settings extends BukkitStaticClassSerializer {

    public static Boolean ENABLE_AUTO_UPDATER = true;
    public static Integer WEB_PORT = 2221;
    public static Integer CONFIG_VERSION = 1;
    public static ArrayList<String> excludedExtensions = new ArrayList<>();
    public static ArrayList<String> excludedPaths = new ArrayList<>();

    public Settings(File f, BukkitLogger logger) {
        super(Settings.class, f, logger);
    }
    public static boolean isExcluded(File f) {
        if(excludedExtensions.contains(FilenameUtils.getExtension(f.getName()))) return true;
        if(excludedPaths.contains(f.getAbsolutePath())) return true;
        return false;
    }

}
