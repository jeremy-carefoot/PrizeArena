package com.bluecreeper111.prizearena.action;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import com.bluecreeper111.prizearena.Main;
import com.bluecreeper111.prizearena.api.Api;
import com.bluecreeper111.prizearena.api.CooldownManager;
import com.bluecreeper111.prizearena.commands.PrizeJoinCMD;

public class ArenaComplete {
	
	public RewardHandler rewards = new RewardHandler();
	public static void finishArena(String arena) {
		String message = ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("arenas." + arena + ".endMessage"));
		String name = null;
		List<UUID> remove = new ArrayList<>();
		for (UUID id : PrizeJoinCMD.joinedPlayers.keySet()) {
			if (PrizeJoinCMD.joinedPlayers.get(id).equals(arena)) {
				name = Bukkit.getPlayer(id).getName();
				remove.add(id);
				new RewardHandler().giveReward(id);
				break;
			}
		}
		Api.wipePlayers(remove, arena);
		CooldownManager.resetArena.add(arena);
		Bukkit.broadcastMessage(message.replaceAll("%player%", name).replaceAll("%arena%", arena));
	}
	public static void cancelArena(String arena) {
		List<UUID> remove = new ArrayList<>();
		for (UUID id : PrizeJoinCMD.joinedPlayers.keySet()) {
			if (PrizeJoinCMD.joinedPlayers.get(id).equals(arena)) {
				remove.add(id);
			}
		}
		Api.wipePlayers(remove, arena);
		CooldownManager.resetArena.add(arena);
		Bukkit.broadcastMessage(Api.getPrefix() + "§ePrizeArena §6" + arena + "§e ended in a tie!");
	}

}
