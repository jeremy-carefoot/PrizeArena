package com.bluecreeper111.prizearena.api;

import com.bluecreeper111.prizearena.commands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class PrizeCommand implements CommandExecutor {
	
	private final String commandName;
	private final String permission;
	private final boolean consoleUse;
	public static JavaPlugin plugin;
	
	public PrizeCommand(String commandName, String permission, boolean consoleUse) {
		this.commandName = commandName;
		this.permission = permission;
		this.consoleUse = consoleUse;
		plugin.getCommand(commandName).setExecutor(this);
	}
	public final static void registerCommands(JavaPlugin pl) {
		plugin = pl;
		new PACmd();
		new ArenaCMD();
		new PrizeJoinCMD();
		new PrizeLeaveCMD();
		new PAKitCMD();
	}
	
	public abstract void execute(CommandSender sender, Command cmd, String label, String[] args);
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.getName().equalsIgnoreCase(commandName)) {
			return true;
		}
		if (!(sender instanceof Player) && !consoleUse) {
			Api.notPlayer(sender);
			return true;
		}
		if (!sender.hasPermission(permission)) {
			Api.noPermission(sender);
			return true;
		}
		execute(sender, cmd, label, args);
		return true;
		
	}
	

}
