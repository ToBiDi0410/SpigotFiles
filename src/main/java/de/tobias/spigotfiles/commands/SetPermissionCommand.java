package de.tobias.spigotfiles.commands;

import de.tobias.spigotfiles.Main;
import de.tobias.spigotfiles.configs.User;
import de.tobias.spigotfiles.configs.UserPermission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class SetPermissionCommand  implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        if(!(cs instanceof ConsoleCommandSender)) {
            cs.sendMessage(Main.pl.mainLogger.prefix + "§cCommands can only be executed by console (for security reasons)");
            return true;
        }

        if(args.length != 3) {
            cs.sendMessage(Main.pl.mainLogger.prefix + "§cPlease use: /sf-setperm <name> <permission> <yes/no>");
            return true;
        }

        String name = args[0];
        User u = Main.pl.userManager.getByName(name);
        if(u == null) {
            cs.sendMessage(Main.pl.mainLogger.prefix + "§cUser not found!");
            return true;
        }

        String permission = args[1];
        UserPermission perm;
        try {
            perm = UserPermission.valueOf(permission);
        } catch (Exception ex) {
            cs.sendMessage(Main.pl.mainLogger.prefix + "§cPermission not found! (Available: WRITE,READ)");
            return true;
        }

        boolean enable = args[2].equalsIgnoreCase("yes");
        if(enable && !u.permissions.contains(perm)) u.permissions.add(perm);
        if(!enable) u.permissions.remove(perm);
        Main.pl.userManager.save();

        cs.sendMessage(Main.pl.mainLogger.prefix + "§aThe permissions for this user have been updated");
        return true;
    }
}
