package de.tobias.spigotfiles.configs;

import de.tobias.mcutils.bukkit.BukkitLogger;
import de.tobias.mcutils.bukkit.BukkitStaticClassSerializer;

import java.io.File;

public class Settings extends BukkitStaticClassSerializer {

    public static Boolean ENABLE_AUTO_UPDATER = true;
    public static Integer WEB_PORT = 2221;
    public static Integer CONFIG_VERSION = 1;

    public Settings(File f, BukkitLogger logger) {
        super(Settings.class, f, logger);
    }

}
