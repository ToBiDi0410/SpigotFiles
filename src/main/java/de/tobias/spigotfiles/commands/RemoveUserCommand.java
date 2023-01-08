package de.tobias.spigotfiles.commands;

import de.tobias.spigotfiles.Main;
import de.tobias.spigotfiles.configs.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class RemoveUserCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        if(!(cs instanceof ConsoleCommandSender)) {
            cs.sendMessage(Main.pl.mainLogger.prefix + "§cCommands can only be executed by console (for security reasons)");
            return true;
        }

        if(args.length != 1) {
            cs.sendMessage(Main.pl.mainLogger.prefix + "§cPlease use: /sf-removeuser <name>");
            return true;
        }

        String name = args[0];
        User u = Main.pl.userManager.getByName(name);
        if(u == null) {
            cs.sendMessage(Main.pl.mainLogger.prefix + "§cUser not found!");
            return true;
        }

        Main.pl.userManager.users.remove(u);
        Main.pl.userManager.save();

        cs.sendMessage(Main.pl.mainLogger.prefix + "§aThe user has been §cremoved");
        return true;
    }

}
