package de.tobias.spigotfiles.users;

import de.tobias.mcutils.bukkit.BukkitLogger;
import de.tobias.spigotfiles.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserManager {

    public BukkitLogger logger;
    public YamlConfiguration config;
    public File f;
    public ArrayList<User> users;
    public User serverUser;

    public UserManager(File configFile) {
        f = configFile;
        logger = new BukkitLogger("§1UserManager §7| ", Main.pl.mainLogger);
        users = new ArrayList<>();

        serverUser = new User("Server");
        serverUser.ID = "SERVER";
        serverUser.passwordHash = "";
        serverUser.permissions.add(UserPermission.WRITE);
        serverUser.permissions.add(UserPermission.READ);
        users.add(serverUser);
    }

    public User getByName(String username) {
        return users.stream().filter(a -> a.username.equalsIgnoreCase(username)).findAny().orElse(null);
    }

    public boolean init() {
        logger.info("Loading configuration from '" + f.getAbsolutePath() + "'...");

        if(!f.exists()) {
            try {
                f.getParentFile().mkdirs();
                f.createNewFile();
                logger.warn("Empty configuration created");

            } catch (Exception ex) {
                logger.error("Failed to create empty configuration:");
                ex.printStackTrace();
                return false;
            }

            config = YamlConfiguration.loadConfiguration(f);
            logger.info("§aConfiguration loaded!");
            save();
            return true;
        }


        config = YamlConfiguration.loadConfiguration(f);
        ConfigurationSection usersRoot = config.getConfigurationSection("users");
        if(usersRoot == null) {
            logger.error("Invalid configuration: 'users' is null");
            return false;
        }

        for(String username : usersRoot.getKeys(false)) {
            ConfigurationSection userSection = usersRoot.getConfigurationSection(username);
            addUserFromSection(username, userSection);
        }

        logger.info("§aConfiguration loaded!");
        return true;
    }

    public boolean save() {
        logger.info("Saving configuration...");
        config.createSection("users", new HashMap<>());
        for(User u : users) {
            if(!u.ID.equalsIgnoreCase(serverUser.ID)) {
                config.set("users." + u.username + ".id", u.ID);
                config.set("users." + u.username + ".passwordHash", u.passwordHash);

                ArrayList<String> permissions = new ArrayList<>();
                for(UserPermission permission : u.permissions) {
                    permissions.add(permission.name());
                }
                config.set("users." + u.username + ".permissions", permissions);
            }
        }

        try {
            config.save(f);
            logger.info("§aConfiguration saved");
            return true;
        } catch (Exception ex) {
            logger.error("§cFailed to save configuration:");
            ex.printStackTrace();
            return false;
        }
    }

    public void addUserFromSection(String username, ConfigurationSection userSection) {
        if(!userSection.contains("id")) {
            logger.warn("Invalid configuration for '" + username + "': id missing");
            return;
        }

        if(!userSection.contains("passwordHash")) {
            logger.warn("Invalid configuration for '" + username + "': passwordHash missing");
            return;
        }

        if(!userSection.contains("permissions")) {
            logger.warn("Invalid configuration for '" + username + "': permissions missing");
            return;
        }

        User u = new User(username);
        u.ID = userSection.getString("id");
        u.passwordHash = userSection.getString("passwordHash");

        for(String permissionName : userSection.getStringList("permissions")) {
            try {
                UserPermission userPermission = UserPermission.valueOf(permissionName);
                u.permissions.add(userPermission);
            } catch (Exception ex) {
                logger.warn("Unknown permission of '" + username + "': " + permissionName);
            }
        }

        users.add(u);

        logger.info("Imported user: §6" + username);
    }
}
