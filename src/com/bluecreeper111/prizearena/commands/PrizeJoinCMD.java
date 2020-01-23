package com.bluecreeper111.prizearena.commands;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bluecreeper111.prizearena.action.ArenaStarted;
import com.bluecreeper111.prizearena.api.Api;
import com.bluecreeper111.prizearena.api.CooldownManager;
import com.bluecreeper111.prizearena.api.PrizeCommand;

public class PrizeJoinCMD extends PrizeCommand {

	public static HashMap<UUID, String> joinedPlayers = new HashMap<>();

	public PrizeJoinCMD() {
		super("prizejoin", "prizearena.join", false);
	}

	@Override
	public void execute(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if ((ArenaCMD.arenas.getConfigurationSection("arenas") == null)
				|| ArenaCMD.arenas.getConfigurationSection("arenas").getKeys(false).size() == 0) {
			p.sendMessage(Api.getPrefix() + "§cThere is currently no arenas!");
			return;
		} else {
			if (ArenaCMD.arenas.getConfigurationSection("arenas").getKeys(false).size() == 1) {
				for (String arena : ArenaCMD.arenas.getConfigurationSection("arenas").getKeys(false)) {
					if (ArenaStarted.arenasInAction.contains(arena)) {
						p.sendMessage(Api.getPrefix() + "§cThe PrizeArena is currently underway! Try joining later.");
						return;
					}
					if (joinedPlayers.containsKey(p.getUniqueId())) {
						p.sendMessage(Api.getPrefix() + "§cYou are already in the PrizeArena!");
						return;
					}
					joinedPlayers.put(p.getUniqueId(), arena);
					p.sendMessage(Api.getPrefix() + "§aSuccessfully joined the PrizeArena! Starting in: §2"
							+ CooldownManager.cooldowns.get(arena) + " seconds");
					return;
				}
			} else if (ArenaCMD.arenas.getConfigurationSection("arenas").getKeys(false).size() > 1
					&& !(args.length > 0)) {
				Api.incorrectSyntax(p, "/" + label + " <arena>", false);
				return;
			} else if (ArenaCMD.arenas.getConfigurationSection("arenas").getKeys(false).size() > 1 && args.length > 0) {
				for (String arena : ArenaCMD.arenas.getConfigurationSection("arenas").getKeys(false)) {
					if (arena.equals(args[0])) {
						if (ArenaStarted.arenasInAction.contains(args[0])) {
							p.sendMessage(Api.getPrefix() + "§cArena §4" + arena
									+ " §cis currently underway! Try joining later.");
							return;
						}
						if (joinedPlayers.containsKey(p.getUniqueId())) {
							String joinedarena = joinedPlayers.get(p.getUniqueId());
							p.sendMessage(
									Api.getPrefix() + "§cYou have already joined the arena §4" + joinedarena + " §c!");
							return;
						}
						joinedPlayers.put(p.getUniqueId(), arena);
						p.sendMessage(Api.getPrefix() + "§aSuccessfully joined the arena §2" + arena
								+ "§a! Starting in: §2" + CooldownManager.cooldowns.get(arena) + " seconds");
						return;
					} 
				}
				p.sendMessage(Api.getPrefix() + "§cArena §4" + args[0] + "§c was not found!");
				return;
			} else {
				Api.incorrectSyntax(p, "/" + label + " <arena>", false);
				return;
			}
		}

	}

}
