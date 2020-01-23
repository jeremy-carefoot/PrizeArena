package com.bluecreeper111.prizearena.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bluecreeper111.prizearena.api.Api;
import com.bluecreeper111.prizearena.api.PrizeCommand;

public class PACmd extends PrizeCommand {

	public PACmd() {
		super("prizearena", "", true);
	}

	public void execute(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			if (!sender.hasPermission("prizearena.info")) {
				Api.noPermission((Player)sender);
				return;
			}
			sender.sendMessage("§e§l[PrizeArena] §aDeveloped by bluecreeper111. Currently running version V" + plugin.getDescription().getVersion());
			return;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
			if (!sender.hasPermission("prizearena.reload")) {
				Api.noPermission((Player)sender);
				return;
			} else {
				plugin.reloadConfig();
				sender.sendMessage(Api.getPrefix() + "§aConfiguration file reloaded.");
				return;
			}
			
		} else {
			Api.incorrectSyntax(sender, "/" + label +" [reload]", true);
			return;
		}
		
	}
	
	
	
	

}
