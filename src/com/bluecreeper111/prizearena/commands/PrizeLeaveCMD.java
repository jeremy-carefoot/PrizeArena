package com.bluecreeper111.prizearena.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bluecreeper111.prizearena.action.ArenaStarted;
import com.bluecreeper111.prizearena.api.Api;
import com.bluecreeper111.prizearena.api.PrizeCommand;

public class PrizeLeaveCMD extends PrizeCommand {
	
	public PrizeLeaveCMD() {
		super ("prizeleave", "prizearena.leave", false);
	}

	
	public void execute(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if (PrizeJoinCMD.joinedPlayers.containsKey(p.getUniqueId())) {
			String arena = PrizeJoinCMD.joinedPlayers.get(p.getUniqueId());
			if (ArenaStarted.arenasInAction.contains(arena)) {
				p.sendMessage(Api.getPrefix() + "§cCannot leave during an arena!");
				return;
			}
			p.sendMessage(Api.getPrefix() + "§6You have left arena §c" + arena);
			PrizeJoinCMD.joinedPlayers.remove(p.getUniqueId());
			return;
		} else {
			p.sendMessage(Api.getPrefix() + "§cYou are not in an arena!");
			return;
		}
				
		
	}
	
	

}
