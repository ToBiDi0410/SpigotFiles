package de.tobias.spigotfiles.commands;

import de.tobias.spigotfiles.Main;
import de.tobias.spigotfiles.configs.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class ChangePasswordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        if(!(cs instanceof ConsoleCommandSender)) {
            cs.sendMessage(Main.pl.mainLogger.prefix + "§cCommands can only be executed by console (for security reasons)");
            return true;
        }

        if(args.length != 2) {
            cs.sendMessage(Main.pl.mainLogger.prefix + "§cPlease use: /sf-changepass <name> <new password>");
            return true;
        }

        String name = args[0];
        User u = Main.pl.userManager.getByName(name);
        if(u == null) {
            cs.sendMessage(Main.pl.mainLogger.prefix + "§cUser not found!");
            return true;
        }

        String password = args[1];
        if(password.length() < 8) {
            cs.sendMessage(Main.pl.mainLogger.prefix + "§cThe password needs to be at least 8 characters long");
            return true;
        }

        u.updatePassword(password);
        Main.pl.userManager.save();

        cs.sendMessage(Main.pl.mainLogger.prefix + "§aThe password has been updated");
        return true;
    }

}